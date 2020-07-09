package com.example.plugctrl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    protected static String IP;
    protected static String aux;





    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;


        public TextView textView;

        public MyViewHolder(Context context, View v) {
            super(v);
            textView = v.findViewById(R.id.on_off);
            this.context = context;
            v.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {

            String IP_plugNumber = null;
            int position = getAdapterPosition(); // gets item position
            System.out.println("valor de position"+position+"\n");
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it

                IP_plugNumber= IP + " " + position;
                Intent goToNextMenu = new Intent( context.getApplicationContext() ,PlugSettingsActivity.class );
                goToNextMenu.putExtra("IP_plugNumber",IP_plugNumber);
                context.startActivity(goToNextMenu);


            }



        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList myDataset , String IP ) {
        this.mDataset = myDataset;
        this.IP = IP;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View nameView = inflater.inflate(R.layout.my_text_view, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(context, (nameView));
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String myuser = mDataset.get(position);
        TextView tv = holder.textView;
        tv.setText(myuser);


    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

