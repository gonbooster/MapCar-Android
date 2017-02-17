package com.example.gon.mando;

/**
 * Created by Gon on 11/12/2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;

public class TcpClient {

    private static final String TAG = TcpClient.class.getSimpleName();

    private Socket socket;
    private Button button;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;
    private String data;

    public TcpClient() {
        socket = null;
        out = null;
        in = null;
        connected = false;
    }

    public String getData (){
        return this.data;
    }
    public void setButton(Button b){
        this.button= b;
    }

    public void connect(Context context, String host, String port) {
        if (!isConnected())
            new ConnectTask(context).execute(host, port);
    }

    public boolean isConnected(){
        return connected;
    }

    public void textButton(String text){
        button.setText(text);
        /*if (text.equalsIgnoreCase("Disconnected"))
            button.setTextColor(Color.RED);
        else if (text.equalsIgnoreCase("Connected"))
            button.setTextColor(Color.BLUE);
            */
    }


    private class ConnectTask extends AsyncTask<String, Void, Void> {

        private Context context;

        public ConnectTask(Context context) {
            this.context = context;
        }

       /* @Override
        protected void onPreExecute() {
            showToast(context, "Connecting..");
            super.onPreExecute();
        }*/

        @Override
        protected void onPostExecute(Void result) {
            if (isConnected())
            {
                //showToast(context, "Connection successful");
                textButton("Disconnect");
            }
            else {
                showToast(context, "Connection refused");
                textButton("Connect");
            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String host = params[0];
                int port = Integer.parseInt(params[1]);
                InetAddress serverAddr = InetAddress.getByName(host);
                socket = new Socket(serverAddr, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                connected = true;
            } catch (UnknownHostException e) {
                showToast(context, "Don't know about host");
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                showToast(context, "Couldn't get I/O for the connection");
                Log.e(TAG, e.getMessage());
            }
            return null;
        }


    }

    public void disconnect(Context context) {
        if (isConnected()) {
            try {
                send(":stop:0:0;");
                out.close();
                in.close();
                socket.close();
                connected = false;
                textButton("Connect");
            } catch (IOException e) {
                showToast(context, "Couldn't get I/O for the connection");
                Log.e(TAG, e.getMessage());
            }
        }
    }


    /**
     * Send command to a Pure Data audio engine.
     */
    public void send(String command)
    {
        if (isConnected()){
            out.println(command);
            out.flush();

        }
    }

    public void readString(Context context) {
        if (isConnected())
            new ReciveTask(context).execute();
    }

    private class ReciveTask extends AsyncTask<String, Void, String> {

        private Context context;

        public ReciveTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            try {

                    int aux = 0;
                    char[] buffer = new char[200];
                    aux = in.read(buffer, 0, buffer.length);
                    msg = new String(buffer, 0, aux);
                    data=msg;

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                msg = "Lat: 0 Lon: 0";
            }
            return msg;

        }
    }

    private void showToast(final Context context, final String message) {
        new Handler(context.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}