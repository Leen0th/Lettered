/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clientphase1game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author developer
 */
public class LetteredServer {
    
    
    Timer timer;
    boolean gamestarted;
    
    //array Includes clients who click "connect" 
    public static ArrayList<LetteredServerThread> Clients = new ArrayList<>();
    
   public static void main(String[] args) throws IOException {
    LetteredServer serverInstance = new LetteredServer(); // Create an instance of UnScrambleServer
    serverInstance.acceptClientConnections(); // Call method to start the server
}
        
// This method starts the server and continuously waits for client connections
public void acceptClientConnections() throws IOException {
    // Create a server socket to listen for incoming client connections
    ServerSocket serverSocket = new ServerSocket(5000, 1, InetAddress.getByName("localhost"));
    
    timer= new Timer();
    gamestarted= false;
    
    // Enter an infinite loop to continuously accept new clients
    while (true) {
        // Display a message to indicate that the server is waiting for a new client
        System.out.println("Server is ready for client connections...");
       
        // Accept a new client connection when it is received
        Socket clientSocket = serverSocket.accept();
       
        // Create a new thread to handle the connected client
        LetteredServerThread newClientThread = new LetteredServerThread(this, clientSocket);
       
        // Add the new client thread to the list of active clients
        Clients.add(newClientThread);
       
        // Start the thread to begin handling client requests
        newClientThread.start();
    }
} // end acceptClientConnections()

 
 
 public void ClientStatus() {
    // Initialize the data string to compile status of all clients.
    String data = "";

    // Build a status message for each client based on whether they are currently playing.
    for (LetteredServerThread client : LetteredServer.Clients) {
        data += client.username + "[Points: " + client.Points + "]" + 
                (client.StartPlay ? "Playing #" : "Connected #");
    }

    // Send the compiled data to all clients and ensure it's flushed immediately.
    for (LetteredServerThread client : LetteredServer.Clients) {
        client.output.println(data);
        
    }
}
   

    public void GameResults(String username) {
    // Stop the timer associated with game activities.
    timer.cancel();

    // Initialize the data string to compile overall game results.
    String data = "";
    String endMessage;

    // Iterate over each client to compile their end game status and send individual messages.
    for (LetteredServerThread client : Clients) {
        // Append the basic client information.
        data += client.username + "[Points: " + client.Points + "]";

        // Check if the player has started playing and if their score is 5 
        if (client.StartPlay && client.Points == 5) {
            // The client played and scored at least 5, so mark them as a Winner.
            data += "Winner #";
            endMessage = "Game ended you won";
        } else {
            // The client either did not start playing or did not score enough, so mark them as a Loser.
            data += "Loser #";
            endMessage = "Game ended you LOST";
        }

        // Send individual conclusion message.
        client.output.println(endMessage);
        client.output.flush();
    }

    // Broadcast the compiled data to all clients in the server.
    for (LetteredServerThread client : LetteredServer.Clients) {
        client.output.println(data);
        client.output.flush();
    }
}

    public void PalyersInfo(String username) {
   
    // Initialize the data string to compile the status of all clients.
    String data = "";

    for (LetteredServerThread item : LetteredServer.Clients) {
            if (item.StartPlay) {
                if (item.username.equals(username) || item.Points >= 5) {
                    data += item.username + "[Points: " + item.Points + "]" + "Winner #";
                } else {
                    data += item.username + "[Points: " + item.Points + "]" + "Loser #";
                }
            } else {
                data += item.username + "[Points: " + item.Points + "]" + "Connected #";
            }
        }

    // Send the compiled data to all clients and ensure it's flushed immediately.
    for (LetteredServerThread client : LetteredServer.Clients) {
        client.output.println(data);
        client.output.flush();
    }
}

    
   public boolean startGame() throws InterruptedException {

    int playerCount = countPlayersReadyToPlay();
    ClientInfo();
    switch (playerCount) {

        case 0:

        case 1:

            notifyPlayers("Waiting for other players.");

            break;

        case 2:

            notifyPlayers("Number of current players: 2. Waiting for 30 seconds.");

            Thread.sleep(30000);

            for (LetteredServerThread player : LetteredServer.Clients) {

                if (player.StartPlay && !gamestarted) {

                    notifyPlayers("Try to extract 5 words from these letters: Adrsowlmenbrit");

                    gamestarted = true;

                }

            }

            break;

        case 3:

            for (LetteredServerThread player : LetteredServer.Clients) {

                if (player.StartPlay) {

                    player.output.println("Try to extract 5 words from these letters:");
                    player.output.println("Adrsowlmenbrit");

                    player.output.flush();

                    gamestarted = true;

                }

            }

            break;

        default:

            notifyPlayers("Sorry, cannot start. Players are maximum.");

            break;

    }


    return gamestarted;

}


private int countPlayersReadyToPlay() {

    int count = 0;

    for (LetteredServerThread player : LetteredServer.Clients) {

        if (player.StartPlay) {

            count++;

        }

    }

    return count;

}


private void notifyPlayers(String message) {

    for (LetteredServerThread player : LetteredServer.Clients) {

        if (player.StartPlay) {

            player.output.println(message);

            player.output.flush();

        }

    }

}



    public void ClientInfo() {
        String data = "";
        for (LetteredServerThread item : LetteredServer.Clients) {
            if (item.StartPlay) {
                data += item.username + "[Points: " + item.Points + "]" + "Playing #";
            } else {
                data += item.username + "[Points: " + item.Points + "]" + "Connected #";
            }
        }
        for (LetteredServerThread item : LetteredServer.Clients) {

            item.output.println(data);
            item.output.flush();

        }

    }

    // Send a notification to all clients about a play request from a specific user
public void notifyPlayRequest(String requesterUsername) {
    // Iterate over all connected clients
    for (LetteredServerThread client : Clients) {
        // Only notify clients who are actively playing
        if (client.StartPlay) {
            // Inform them about the play request from the specified user
            client.output.println("Alert: User " + requesterUsername + " has made a play request.");
        }
    }
} // end of notifyPlayRequest

   
}

