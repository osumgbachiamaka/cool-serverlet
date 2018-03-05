package com.example.android.locationverification;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class MainActivity extends Activity {

    static final int BUFFER_SIZE = 4096;
    String file;
    File uploadFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Recording/Record002.3gpp";
        uploadFile = new File(file);
        if(uploadFile.exists()){
            TextView text = (TextView)findViewById(R.id.textview);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new sendFile().execute(new String[] { "http://192.168.43.125:8080/ZeroC/ReceiveAudio" });
                }
            });
        }

    }
    private class sendFile extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection httpConn=null;

            try
            {

                FileInputStream inputStream = new FileInputStream(uploadFile);

                System.out.println("File to upload: " + file);

                // creates a HTTP connection
                URL url1 = new URL(urls[0]);
                httpConn = (HttpURLConnection) url1.openConnection();
                httpConn.setUseCaches(false);
                httpConn.setDoOutput(true);
//                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("fileName", uploadFile.getName());
                httpConn.connect();
                // sets file name as a HTTP header

                Log.i("fileName", uploadFile.getName());
                // opens output stream of the HTTP connection for writing data

                OutputStream outputStream = httpConn.getOutputStream();

                // Opens input stream of the file for reading data


                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;

                System.out.println("Start writing data...");

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("Data was written.");
                Log.d("responsecode", ""+httpConn.getResponseCode());
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String myResponse = in.readLine();
                Log.d("my response", myResponse);
                outputStream.close();
                inputStream.close();
            }
            catch(SocketTimeoutException e)
            {
                Log.e("Debug", "error: " + e.getMessage(), e);
            }
            catch (MalformedURLException ex)
            {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            }
            catch (IOException ioe)
            {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }

//            try
//            {
//
//                // always check HTTP response code from server
//                int responseCode = httpConn.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // reads server's response
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
//                    String response = reader.readLine();
//                    System.out.println("Server's response: " + response);
//                } else {
//                    System.out.println("Server returned non-OK code: " + responseCode);
//                }
//            }
//            catch (IOException ioex){
//                Log.e("Debug", "error: " + ioex.getMessage(), ioex);
//            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
