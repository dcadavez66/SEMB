package com.example.plugctrl;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.*;

import com.example.chatroom.ClientActivity;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;


public class ConfigActivity extends AppCompatActivity {


    TextView instructions;
    HTTPActivity httpObj;
    EditText text_ssid;
    EditText text_password;
    Button submitBt;
    String ssid  = null;
    String password = null;
    String IP = "192.168.4.1";
    String httpHeader = "http://";
    int port = 80;
    String request = null;
    String receivedMessage = null;
    ThreadReceive threadReceive = null;
    //ThreadSend threadSend = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);


        httpObj = new HTTPActivity();
        //threadSend = new ThreadSend();
        threadReceive = new ThreadReceive(httpObj);
        threadReceive.startRunning();
        text_ssid = findViewById(R.id.ssid);
        text_password = findViewById(R.id.password);
        submitBt = findViewById(R.id.submitBt);
        instructions = findViewById(R.id.instructions);

        instructions.setText("\tInstructions\n\r" +
                "1º-Connect your phone to the ESP_SERVER Network.\n\r" +
                "2º-Insert your domestic network credentials, this will connect the plug to the WiFi ");

        //codigo funcional , abre o browser
        /*Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String message = IP + "/";
        intent.putExtra(SearchManager.QUERY, message);
        startActivity(intent);*/
        request = httpHeader + IP +"/";
        threadReceive.setMessage(request);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Mensagem Recebida:"+threadReceive.getResp());

            submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ssid = text_ssid.getText().toString().trim();
                password = text_password.getText().toString().trim();

                if(!ssid.isEmpty() && !password.isEmpty()){
                    threadReceive.startRunning();
                    request = httpHeader + IP + "/config?USERNAME=" + ssid + "&PASSWORD=" + password;
                    threadReceive.setMessage(request);
                }

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                receivedMessage = threadReceive.getResp();
                System.out.println("Recebida"+receivedMessage);
                //receivedMessage = "ack";
                if(receivedMessage == "failed"){
                    Toast.makeText(ConfigActivity.this , "Failed to Connect",Toast.LENGTH_SHORT).show();

                }
                else if(isValidIPAddress(receivedMessage)){
                    Toast.makeText(ConfigActivity.this , "Connected",Toast.LENGTH_SHORT).show();
                    Intent nextScreen = new Intent(getApplicationContext(), ControlActivity.class);
                    nextScreen.putExtra("IP", receivedMessage);

                    startActivity(nextScreen);
                }


            }
        });


    }

    public static boolean isValidIPAddress(String ip)
    {


        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";


        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;


        Pattern p = Pattern.compile(regex);


        if (ip == null) {
            return false;
        }


        Matcher m = p.matcher(ip);

        return m.matches();
    }




}
