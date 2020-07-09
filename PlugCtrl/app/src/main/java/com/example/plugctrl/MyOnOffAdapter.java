package com.example.plugctrl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyOnOffAdapter extends RecyclerView.Adapter<MyOnOffAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    protected static String IP;
    protected static String aux;




    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        boolean button_state = false;
        HTTPActivity httpObj;
        String httpHeader = "http://";
        String request = null;
        String receivedMessage = null;
        ThreadReceive threadReceive = null;

        public TextView textView;

        public MyViewHolder(Context context, View v) {
            super(v);
            textView = v.findViewById(R.id.on_off);
            this.context = context;
            httpObj = new HTTPActivity();
            threadReceive = new ThreadReceive(httpObj);

            v.setOnClickListener(this);



        }


        @Override
        public void onClick(View view) {


            int position = getAdapterPosition(); // gets item position
            System.out.println("valor de position"+position+"\n");
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it

                if( button_state == false){
                    threadReceive.startRunning();
                    request = httpHeader + IP +"/on?PLUGNUMBER="+position;
                    threadReceive.setMessage(request);
                    view.setActivated(true);
                    button_state = true;
                }
                else if(button_state == true){
                    threadReceive.startRunning();
                    request = httpHeader + IP +"/off?PLUGNUMBER="+position;
                    threadReceive.setMessage(request);
                    view.setActivated(false);
                    button_state = false;
                }



            }



        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyOnOffAdapter(ArrayList myDataset,String IP ) {
        this.IP = IP;
        this.mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyOnOffAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View nameView = inflater.inflate(R.layout.my_on_off, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(context, (nameView));
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String myuser = mDataset.get(position);
        //TextView tv = holder.textView;
        //tv.setText(myuser);


    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

