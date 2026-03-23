package com.example;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program should allow the client to send it messages. The messages should then 
 * become visible to all other clients.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program creates a new thread for
 * every connection request.
 */
public class ChatServerWithThreads {

    public static final int LISTENING_PORT = 9876;

    public static void main(String[] args) {

        ServerSocket listener;  // Listens for incoming connections.
        Socket connection;      // For communication with the connecting program.

        /* Accept and process connections forever, or until some error occurs. */

        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                  // Accept next connection request and handle it.
                  connection = listener.accept();
                  ConnectionHandler h = new ConnectionHandler(connection);
                  h.start();
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }
    }  // end main()


    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    private static class ConnectionHandler extends Thread {
        private static volatile ArrayList<ConnectionHandler> handlers;
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;

        ConnectionHandler(Socket socket) {
            client = socket;
            if(handlers == null){
                handlers = new ArrayList();
            }
            handlers.add(this);
            try {
                oos = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                ois = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }

        public void sendMessage(String message){
            for(int i = 0; i < handlers.size(); i++){
                if(handlers.get(i) != this){
                    ObjectOutputStream stream = handlers.get(i).oos;
                    try {
                        stream.writeObject(message);
                        stream.flush();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

        public void run() {
            String clientAddress = client.getInetAddress().toString();
            while(true) {
	            try {
                String message = (String) ois.readObject();
                if(!message.equals("disconnect")){
                    System.out.println("Message Received from a Client: " + message);
                    //tell every other connenction handler to send this message
                    sendMessage(message);
                }
                else{
                    System.out.println("Closing connection");
                    break;
                }
	            }
	             catch(EOFException e){
                    System.out.println("the client disconnected, bye!!!");
                    handlers.remove(this);
                    break;
                }
                catch (Exception e){
                    System.out.println("Error on connection with: " + clientAddress + ": " + e);
	            }
            }
        }
    }
}