package com.example.chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientActivity implements Serializable{

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    int port;
    String IP = "";
    String signin = "sin";
    String ack = "ack";
    String nack = "nack";
    String send = "snd";
    String friend = "frd";
    String newconversation = "ncv";
    String username ;
    // static String[] identified_message = new String[2];

    public void startConnection(String IP , int port)throws UnknownHostException, IOException {

        clientSocket = new Socket( IP , port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


    }

    public static String getAddress() {

        String address = "";
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            address = socket.getLocalAddress().getHostAddress();
            socket.close();
        }
        catch(Exception e){
            System.out.println("Failed to get local address");
        }
        return address;
    }

    public String sendMessage(String[] msg) throws IOException {
        out.println(msg);
        //String resp = in.readLine();
        return null;
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        //String resp = in.readLine();
        return null;
    }

    public String receiveMessage() throws IOException {

        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }




}

