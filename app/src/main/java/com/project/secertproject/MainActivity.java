package com.project.secertproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;




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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.output);

    }
}
