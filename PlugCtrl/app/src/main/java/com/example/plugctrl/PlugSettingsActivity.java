package com.example.plugctrl;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class PlugSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Button saveButton;
    Button resetButton;
    Spinner spinner;
    HTTPActivity httpObj;
    ArrayList<String> device_types;
    ArrayAdapter<String > adapter;
    Intent j;
    String IP = null;
    int plugNumber = -1;
    int plugType = -1;
    String[] IP_plugNumber = null;
    String receiveIntent = null;
    String httpHeader = "http://";
    String request = null;
    String receivedMessage = null;
    boolean button_state = false;
    ThreadReceive threadReceive = null;
    String device = null;
    SeekBar minTemperature;
    SeekBar maxTemperature;
    TextView textMinT;
    TextView valueMinT;
    TextView textMaxT;
    TextView valueMaxT;
    TimePicker timePicker;
    TimePicker timePicker2;
    TextView textOff;
    TextView textOn;
    TextView horas;

    int TemperatureMin = -1;
    int auxTemperatureMin = -1;
    int TemperatureMax = -1;
    int auxTemperatureMax = -1;

    int OnHours = -1;
    int auxOnHours = -1;
    int OnMinutes = -1;
    int auxOnMinutes = -1;
    int OffHours = -1;
    int auxOffHours = -1;
    int OffMinutes = -1;
    int auxOffMinutes = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_settings);


        j = getIntent();
        receiveIntent= j.getStringExtra("IP_plugNumber");
        IP_plugNumber = receiveIntent.split(" ", 2);
        IP = IP_plugNumber[0];
        plugNumber = Integer.parseInt(IP_plugNumber[1]);
        System.out.println("IP domestic:" + IP);
        System.out.println("Plug Number:" + plugNumber);
        httpObj = new HTTPActivity();
        threadReceive = new ThreadReceive(httpObj);
        spinner = findViewById(R.id.spinner);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);

        minTemperature = findViewById(R.id.minTemperature);
        textMinT = findViewById(R.id.text_min_T);
        minTemperature.setVisibility(View.INVISIBLE);
        textMinT.setVisibility(View.INVISIBLE);

        maxTemperature = findViewById(R.id.maxTemperature);
        textMaxT = findViewById(R.id.text_max_T);
        maxTemperature.setVisibility(View.INVISIBLE);
        textMaxT.setVisibility(View.INVISIBLE);

        timePicker = findViewById(R.id.datePicker1);
        timePicker.setIs24HourView(true);
        timePicker.setVisibility(View.INVISIBLE);



        timePicker2 = findViewById(R.id.datePicker2);
        timePicker2.setIs24HourView(true);
        timePicker2.setVisibility(View.INVISIBLE);

        textOff = findViewById(R.id.text_off);
        textOff.setVisibility(View.INVISIBLE);

        textOn = findViewById(R.id.text_on);
        textOn.setVisibility(View.INVISIBLE);


        minTemperature.setOnSeekBarChangeListener(seekBarChangeListenerMin);
        maxTemperature.setOnSeekBarChangeListener(seekBarChangeListenerMax);

        timePicker.setOnTimeChangedListener(timeChangedListener);
        timePicker2.setOnTimeChangedListener(timeChangedListener2);




        textMinT.setText("Min Cº: 0" );
        textMaxT.setText("Max Cº: 0");
        device_types = new ArrayList<>();
        device_types.add("None");
        device_types.add("Heater");
        device_types.add("Lamp");
        device_types.add("Charger");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item , device_types);
         //adapter = ArrayAdapter.createFromResource(this, device_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                threadReceive.startRunning();
                request = httpHeader + IP + "/plugSetup?TEMPERATUREMIN=" + TemperatureMin + "&TEMPERATUREMAX=" +
                        TemperatureMax +"&ONHOURS=" + OnHours + "&ONMINUTES=" + OnMinutes + "&OFFHOURS=" +
                        OffHours + "&OFFMINUTES=" + OffMinutes +"&PLUGNUMBER="  + plugNumber +"&PLUGTYPE=" + plugType;

                threadReceive.setMessage(request);
            }


        });

        resetButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               TemperatureMax = -1;
               TemperatureMin = -1;
               auxTemperatureMax = -1;
               auxTemperatureMin = -1;
               OnHours = -1;
               auxOnHours = -1;
               OnMinutes = -1;
               auxOnMinutes = -1;
               OffHours = -1;
               auxOffHours = -1;
               OffMinutes = -1;
               auxOffMinutes = -1;
               threadReceive.startRunning();
               request = httpHeader + IP + "/ResetT?PLUGNUMBER=" + plugNumber;
               threadReceive.setMessage(request);
           }
       });




    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        device = parent.getItemAtPosition(pos).toString();
        if(device == "Heater"){
            minTemperature.setVisibility(View.VISIBLE);
            textMinT.setVisibility(View.VISIBLE);

            maxTemperature.setVisibility(View.VISIBLE);
            textMaxT.setVisibility(View.VISIBLE);

            timePicker.setVisibility(View.VISIBLE);
            timePicker2.setVisibility(View.VISIBLE);

            textOn.setVisibility(View.VISIBLE);
            textOff.setVisibility(View.VISIBLE);
            plugType = 0;

        }
        else if(device == "Lamp"){
            minTemperature.setVisibility(View.INVISIBLE);
            textMinT.setVisibility(View.INVISIBLE);

            maxTemperature.setVisibility(View.INVISIBLE);
            textMaxT.setVisibility(View.INVISIBLE);

            timePicker.setVisibility(View.VISIBLE);
            timePicker2.setVisibility(View.VISIBLE);

            textOn.setVisibility(View.VISIBLE);
            textOff.setVisibility(View.VISIBLE);

            plugType = 1;


        }
        else if(device == "Charger"){
            minTemperature.setVisibility(View.INVISIBLE);
            textMinT.setVisibility(View.INVISIBLE);

            maxTemperature.setVisibility(View.INVISIBLE);
            textMaxT.setVisibility(View.INVISIBLE);

            timePicker.setVisibility(View.INVISIBLE);
            timePicker2.setVisibility(View.INVISIBLE);

            textOn.setVisibility(View.INVISIBLE);
            textOff.setVisibility(View.INVISIBLE);
            plugType = 2;

        }
        else if(device == "None"){
            minTemperature.setVisibility(View.INVISIBLE);
            textMinT.setVisibility(View.INVISIBLE);

            maxTemperature.setVisibility(View.INVISIBLE);
            textMaxT.setVisibility(View.INVISIBLE);

            timePicker.setVisibility(View.INVISIBLE);
            timePicker2.setVisibility(View.INVISIBLE);

            textOn.setVisibility(View.INVISIBLE);
            textOff.setVisibility(View.INVISIBLE);
            plugType = -1;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListenerMin = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            textMinT.setText("Min Cº:" + progress );
            auxTemperatureMin = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
            TemperatureMin = auxTemperatureMin;

        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListenerMax = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            textMaxT.setText("Max Cº:" + progress );
            auxTemperatureMax = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
            //enviar info para o ESP
            TemperatureMax = auxTemperatureMax;
        }
    };

    TimePicker.OnTimeChangedListener timeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

            OffHours = hourOfDay;
            OffMinutes = minute;
        }


    };

    TimePicker.OnTimeChangedListener timeChangedListener2 = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

            OnHours = hourOfDay;
            OnMinutes = minute;
        }


    };


}
