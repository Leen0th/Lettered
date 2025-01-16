/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientphase1game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atata
 */
class LetteredServerThread extends Thread {

    LetteredServer server;
    Socket client;
    String username;
    BufferedReader input;
    PrintWriter output;
    String recievedData;
    boolean StartPlay = false;
    int Points = 0;

    LetteredServerThread(LetteredServer server, Socket clientSocket) {
        try {
            this.server = server;
            client = clientSocket;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ex) {
            //Logger.getLogger(ServerProgress.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
public void run() {
    while (true) {
        try {
            while (input != null && !(recievedData = input.readLine()).equals(null)) {
                if (!recievedData.equals(null)) {
                    String command = extractCommand(recievedData); // Helper method to extract command

                    switch (command) {
                        case "end":
                            String data = recievedData.replace("end#", "");
                            System.out.println("Player Client " + username + " is Dissconnected");
                            LetteredServer.Clients.remove(this);
                            this.input.close();
                            this.output.close();
                            this.client.close();
                            this.username = "";
                            server.ClientInfo();
                            break;
                        case "finish_game":
                            this.StartPlay = false;
                            System.out.println("Player Client " + username + " is exit");
                            server.ClientInfo();
                            break;
                        case "Name":
                            username = recievedData.replace("Name:", "");
                            System.out.println("Client " + username + " is Connected");
                            server.ClientStatus();
                            break;
                        case "Play":
                            this.StartPlay = true;
                            username = recievedData.replace("Play:", "");
                            System.out.println("Client " + username + " is requested to play");
                            server.ClientInfo();
                            if (server.startGame()) {
                                server.timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        // Your database code here
                                        System.out.println("Timer ended");
                                        server.PalyersInfo("__");
                                        server.PalyersInfo("__");

                                        try {
                                            Thread.sleep(5000l);
                                        } catch (InterruptedException ex) {
                                        }

                                        server.GameResults("__");
                                        server.gamestarted = false;
                                    }
                                }, 60 * 1000);
                            }
                            break;
                        case "Answer":
                            if (updatescore(recievedData)) {
                                server.PalyersInfo(username);
                                server.PalyersInfo(username);

                                Thread.sleep(5000l);
                                server.GameResults(username);
                                output.println("Congratulations You won");
                                output.flush();
                                server.gamestarted = false;
                            } else {
                                server.ClientInfo();
                            }
                            break;
                        default:
                            server.ClientInfo();
                            break;
                    }
                } else {
                    server.ClientInfo();
                }
            }
        } catch (IOException e) {
            // Handle exception
        } catch (InterruptedException ex) {
            Logger.getLogger(LetteredServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

private String extractCommand(String data) {
    if (data.startsWith("end")) {
        return "end";
    } else if (data.startsWith("finish_game")) {
        return "finish_game";
    } else if (data.startsWith("Name:")) {
        return "Name";
    } else if (data.startsWith("Play:")) {
        return "Play";
    } else if (data.startsWith("Answer")) {
        return "Answer";
    }
    return "unknown";
}


    private boolean updatescore(String userAnswer) {
        // Answer sara bird
        String[] data = userAnswer.split(" ");
        for (LetteredServerThread item : LetteredServer.Clients) {
            if (item.username.equals(data[1])) {
                boolean correct = true;
                for (int i = 0; i < data[2].length(); i++) {
                    if (!"Adrsowlmenbrit".contains(String.valueOf(data[2].charAt(i)))) {
                        correct = false;
                    }
                }
                if (correct) {
                    item.Points++;
                    if (item.Points == 5) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}

