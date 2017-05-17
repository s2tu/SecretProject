package com.project.secertproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.SeekBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {



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

    void connect_to_server(View button){
        server = new Server();
        button.setEnabled(false);

    }
    void send_to_server(View button){
        if(server != null) {
            Map<String, String> data = new HashMap<>();
            data.put("toy_name", "test");
            data.put("command", "test command");
            data.put("value", Integer.toString(1));
            JSONObject json = new JSONObject(data);
            server.send_message(json);
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
                //instead of send to toy we want to send to server here
                //toy name
                //command
                //strgnt
                Map<String, String> data = new HashMap<>();
                data.put("toy_name", toy_manager.toy_name);
                data.put("command", command);
                data.put("value", Integer.toString(i));
                JSONObject json = new JSONObject(data);
                server.send_message(json);

                //toy_manager.send_command(command, i);
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


        connect_to_server = (Button) findViewById(R.id.connect_to_server);
        connect_toy = (Button) findViewById(R.id.connect);
        send = (Button) findViewById(R.id.send);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if(server != null) {
            server.disconnect_server();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();

    }
    public void onEvent(final ServerConnectedEvent event){
        Log.d("EVENT:", "connected");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send.setEnabled(true);
                try {
                    Boolean toy_connected = toy_manager.connect_toy();
                    if(toy_connected){
                        enable_toy_buttons(toy_manager.toy_name);
                        //button.setEnabled(false);
                    }else{
                        output.setText("Toy was unable to connect. Please connect using Body Chat.");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        //
    }
    public void onEvent(final ServerReplyEvent event){
        Log.d("EVENT:", event.getMessage());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(event.getMessage());
                try{
                    JSONObject data = new JSONObject(event.getMessage());
                    toy_manager.send_command(data.getString("command"), Integer.parseInt(data.getString("value")));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        //
    }
    //class global items

    Button send;
    Button connect_to_server;
    Button connect_toy;

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
    Server server;

}
