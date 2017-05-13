package com.project.secertproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    TextView output;
    class Server extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                final Socket socket = IO.socket("http://10.15.202.148:3001");
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        socket.emit("echo", "hello"); //mobile sends a message

                        //socket.disconnect();
                    }

                }).on("echo back", new Emitter.Listener() {  //if mobile recieves echo back then print message

                    @Override
                    public void call(Object... args) {
                        String message = "";
                        for(int i=0;i < args.length; i++){
                            message = message + args[i] + " ";
                        }
                        Log.d("SocketIO", "Message from Server " + message);
                        Log.d("SocketIO", "Message from Server " + args.length);
                        output.setText(message);
                        socket.disconnect();
                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("SocketIO", "disconnected");
                    }

                });
                socket.connect();
            }catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }
    }

   class JsonTask extends AsyncTask{


        protected String doInBackground(Object[] params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL((String)params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            output.setText((String)result);
        }
    }

    void send_to_server(View button){
        try {
            AsyncTask startServer = new Server();
            Log.d("SocketIO", "STARTED");
            startServer.execute();

            //output.setText((String)  startServer.execute().get());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void test_response(View button){
        try {
            AsyncTask start_json_task =  new JsonTask();
            Log.d("JsonTask", "STARTED");
            start_json_task.execute("http://192.168.1.21:34567/GetToys");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.output);

    }
}
