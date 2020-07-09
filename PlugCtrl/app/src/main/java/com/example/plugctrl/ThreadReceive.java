package com.example.plugctrl;

import android.content.Intent;
import com.example.chatroom.ClientActivity;
import java.io.IOException;

public class ThreadReceive implements  Runnable{
    Thread thread;
    String server_resp = null;
    String message = null;
    HTTPActivity client;
    public ThreadReceive(HTTPActivity obj){
        this.client = obj;
    }
    @Override
    public void run(){

        try {
            //client.setGET_URL(this.message);
            //server_resp = client.sendGET();
            client.setPOST_URL(this.message);
            server_resp = client.sendPOST();

        } catch (IOException e) {
            e.printStackTrace();
        }



        thread.interrupt();
    }

    public void startRunning() {

        thread = new Thread (this);
        thread.start();
    }

    public String getResp(){

        return this.server_resp;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
