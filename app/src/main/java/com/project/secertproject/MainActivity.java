package com.project.secertproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.SeekBar;

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

    class Server extends AsyncTask {
        String message = "";
        @Override
        protected String doInBackground(Object[] objects) {
            try {
                final Socket socket = IO.socket("http://192.168.1.18:3001");
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        socket.emit("echo", "hello"); //mobile sends a message

                        //socket.disconnect();
                    }

                }).on("echo back", new Emitter.Listener() {  //if mobile recieves echo back then print message

                    @Override
                    public void call(Object... args) {
                        message = "";
                        for(int i=0;i < args.length; i++){
                            message = message + args[i] + " ";
                        }

                        Log.d("SocketIO", "Message from Server " + message);
                        Log.d("SocketIO", "Message from Server " + args.length);
//                        output.setText(message);
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
            Log.d("SocketIO messsage:", message);
            return "";
        }
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            output.setText((String)result);
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
            Server startServer = new Server();
            Log.d("SocketIO", "STARTED");
            startServer.execute();
            output.setText(startServer.message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void connect_toy(View button){
        try {
            Boolean toy_connected = toy_manager.connect_toy();

            if(toy_connected){
                enable_toy_buttons(toy_manager.toy_name);
                button.setEnabled(false);
            }else{
                output.setText("Toy was unable to connect. Please connect using Body Chat.");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void enable_toy_buttons(String toy_name){

        if(toy_name.equals("max")){
            max_vibrate_txt.setVisibility(View.VISIBLE);
            max_pump_strength_txt.setVisibility(View.VISIBLE);
            max_vibrate.setVisibility(View.VISIBLE);
            max_pump_strength.setVisibility(View.VISIBLE);
        }else if(toy_name.equals("nora")){
            nora_vibrate_txt.setVisibility(View.VISIBLE);
            nora_rotate_speed_txt.setVisibility(View.VISIBLE);
            nora_toggle_dir_txt.setVisibility(View.VISIBLE);
            nora_vibrate.setVisibility(View.VISIBLE);
            nora_rotate_speed.setVisibility(View.VISIBLE);
            nora_toggle_dir.setVisibility(View.VISIBLE);
        }else{
            output.setText("Unable to identify toy");
            Log.d("enable_toy_button:", "Un able to identify toy: $"+ toy_name+ "$");
        }
    }

    SeekBar.OnSeekBarChangeListener createSeakBarListener(final String command){
        return new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("Seekbar " + command, Integer.toString(i));

                toy_manager.send_command(command, i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.output);
        toy_manager = new ToyManager();

        max_vibrate_txt = (TextView) findViewById(R.id.max_vibrate_txt);
        max_pump_strength_txt  = (TextView) findViewById(R.id.max_pump_strength_txt);
        max_vibrate = (SeekBar) findViewById(R.id.max_vibrate);
        max_vibrate.setOnSeekBarChangeListener(createSeakBarListener("Vibrate"));
        max_pump_strength = (SeekBar) findViewById(R.id.max_pump_strength);
        max_pump_strength.setOnSeekBarChangeListener(createSeakBarListener("AirAuto"));

        nora_vibrate_txt = (TextView) findViewById(R.id.nora_vibrate_txt);
        nora_rotate_speed_txt = (TextView) findViewById(R.id.nora_rotate_speed_txt);
        nora_toggle_dir_txt  = (TextView) findViewById(R.id.nora_toggle_dir_txt);
        nora_vibrate = (SeekBar) findViewById(R.id.nora_vibrate);
        nora_rotate_speed = (SeekBar) findViewById(R.id.nora_rotate_speed);
        nora_toggle_dir = (Switch) findViewById(R.id.nora_toggle_dir);
    }

    //class global items
    TextView output;
    TextView max_vibrate_txt;
    TextView max_pump_strength_txt;
    SeekBar max_vibrate;
    SeekBar max_pump_strength;

    TextView nora_vibrate_txt;
    TextView nora_rotate_speed_txt;
    TextView nora_toggle_dir_txt;
    SeekBar nora_vibrate;
    SeekBar nora_rotate_speed;
    Switch nora_toggle_dir;

    ToyManager toy_manager;

}
