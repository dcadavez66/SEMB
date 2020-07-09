package com.example.plugctrl;

import java.io.IOException;
import com.example.chatroom.ClientActivity;
class ThreadSend implements Runnable{
    private Thread thread;
    private String message;
    ClientActivity client;
    public ThreadSend( ClientActivity c) {

        this.client = c;

    }
    @Override
    public void run(){

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void setMessage(String msg) {
        message = msg;
    }
    public void startRunning() {

        thread = new Thread (this);
        thread.start();
    }


}
