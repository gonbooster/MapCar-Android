package com.example.gon.mando;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Gon on 12/12/2015.
 */
public class Txt {

    private static final String txt="ip.txt";

    public void writeToFile(String data, Context ctx) {
        try {

            OutputStreamWriter MyOutputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(txt, ctx.MODE_PRIVATE));
            MyOutputStreamWriter.append(data);
            MyOutputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile(Context ctx) {
        String ret = "192.168.4.1;400;";
        try {
            InputStream inputStream = ctx.openFileInput(txt);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
