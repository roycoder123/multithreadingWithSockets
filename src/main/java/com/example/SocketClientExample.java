package com.example;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class SocketClientExample {
	
	
	/*
	 * Modify this example so that it opens a dialogue window using java swing, 
	 * takes in a user message and sends it
	 * to the server. The server should output the message back to all connected clients
	 * (you should see your own message pop up in your client as well when you send it!).
	 *  We will build on this project in the future to make a full fledged server based game,
	 *  so make sure you can read your code later! Use good programming practices.
	 *  ****HINT**** you may wish to have a thread be in charge of sending information 
	 *  and another thread in charge of receiving information.
	*/
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        final Socket socket = new Socket(host.getHostName(), 9876);
       
        //swing for gui
        JFrame frame = new JFrame("Chat Server");
        JPanel panel = new JPanel();
        JTextField textField = new JTextField(20);
        JButton sendButton = new JButton("Send");
        JButton disconnectButton = new JButton("Disconnect");
        JTextArea textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        scrollPane.setBorder(lineBorder);
        frame.add(panel);
        panel.add(textField);
        panel.add(sendButton);
        panel.add(scrollPane);
        panel.add(disconnectButton);

        frame.setSize(300,300);  
	    frame.setVisible(true);  
        
        //establish socket connection to server
        //socket = new Socket(host.getHostName(), 9876);


        try{
            //establish input and ouput streams
            final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            
            //if text is inputed into the textbox
            textField.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    String text = textField.getText();
                    textField.setText("");
                    try {
                        oos.writeObject(text);
                        textArea.append("     Me: " + text + "\n");
                        oos.flush();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            
            });

            //if disconnect button is clicked
            disconnectButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    try {
                        //close all operations
                        ois.close();
                        oos.close();
                        socket.close();

                        textField.setEditable(false);
                        sendButton.setEnabled(false);
                        disconnectButton.setEnabled(false);

                        textArea.append(".    You have disconnected.\n");
                        oos.flush();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            
            });

            //loop to check for incoming messages
            while(true){
                String message = (String) ois.readObject();
                textArea.append("     Client " + message + "\n");
                System.out.println("Message Received from Server: " + message);
            }
            
        }catch(Exception e){
            System.out.println("Disconnected");
        }
    }
}