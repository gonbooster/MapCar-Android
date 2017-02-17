package com.example.gon.mando;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Gon on 12/12/2015.
 */
public class Address extends AppCompatActivity {

    private Button save;
    private Button cancel;
    private EditText ip;
    private EditText port;
    private Txt txt =new Txt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        setAddress();
        addButtonClickListener();


    }

    public void addButtonClickListener() {
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.writeToFile(ip.getText().toString() + ";" + port.getText().toString() + ";", getApplicationContext());
                Intent intentApp = new Intent(Address.this, MandoActivity.class);
                Address.this.startActivity(intentApp);
            }
        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentApp = new Intent(Address.this, MandoActivity.class);
                Address.this.startActivity(intentApp);
            }
        });

    }
    public void setAddress(){
        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        String all = txt.readFromFile(getApplicationContext());
        String[] separated = all.split(";");
        ip.setText(separated[0]);
        port.setText(separated[1]);
    }


}