package com.example.gon.mando;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.lang.Double;


public class MandoActivity extends AppCompatActivity implements SensorEventListener, OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener  {

    private TcpClient tcpClient= new TcpClient();
    private TextView acelerometer;
    private  Button connect;
    private ImageButton back, forward, stop;
    private SeekBar sb;
    private TextView percent;
    private int position=0;
    private Txt txt = new Txt();
    private String steering= "0";
    private String thottle = "0";
    private String gear = "stop";
    private Double lat = 0.0;
    private Double lon = 0.0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mando);
        addButtonClickListener();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // ever horizontal


        //ACELEROMETER
        acelerometer = (TextView) findViewById(R.id.acelerometer);
        SensorManager sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);
        List<Sensor> listaSensores = sensorManager.
                getSensorList(Sensor.TYPE_ALL);


        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listaSensores.isEmpty()) {
            Sensor acelerometerSensor = listaSensores.get(0);
            sensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }


    //SEEKBAR

        sb = (SeekBar) findViewById(R.id.speed);
        sb.setMax(10);
        sb.setProgress(0);
        sb.setOnSeekBarChangeListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(MandoActivity.this, Address.class);
                MandoActivity.this.startActivity(i);
                //Log.i("Content ", " App layout ");
                break;

        }

        return true;
    }


    @Override
    public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
        percent = (TextView)findViewById(R.id.percent);
        percent.setText(Integer.toString(progress*10)+"%");
        thottle=Integer.toString(progress);
        sendCMD();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSensorChanged(SensorEvent evento) {
        synchronized (this){
            float[] masData;
            int y;
            if(evento.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                masData = evento.values; //masData[0] == x, masData[1] == y, masData[2] == z
                y = (int) masData[1];
                    if (position !=y) { // Dont send repeat values
                        steering = Integer.toString(y);
                        //acelerometer.setText("Steering: " + steering);
                        if (( y <= 6) && ( y >= -6)) {
                            sendCMD();
                        }
                        position = y;
                    }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {
    }

    public void addButtonClickListener() {
        //CONNECT BUTTON
        connect = (Button) findViewById(R.id.connect);
        tcpClient.setButton(connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tcpClient.isConnected()) {
                    String all = txt.readFromFile(getApplicationContext());
                    String[] address = all.split(";");
                    tcpClient.connect(getApplicationContext(), address[0], address[1]);
                } else
                    tcpClient.disconnect(getApplicationContext());

                sb.setProgress(0);
            }
        });
        forward = (ImageButton) findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gear="forward";
                sendCMD();
            }
        });

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gear="back";
                sendCMD();
            }
        });

        stop = (ImageButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gear = "stop";
                sendCMD();
            }
        });
    }
    private void getCoords(String msg)
    {
        String[] words = msg.split(":");
        lat = Double.parseDouble(words[1]);
        lon = Double.parseDouble(words[2]);
        acelerometer.setText("Lat: "+lat+" Lon: "+lon);
    }
    public void sendCMD() {
        if (tcpClient.isConnected()) {
            try {
                tcpClient.send(":" + gear + ":" + thottle + ":" + steering + ";");
                tcpClient.readString(getApplicationContext());
                getCoords(tcpClient.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}