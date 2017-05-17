package com.project.secertproject;
import android.util.Log;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by stu1 on 5/15/2017.
 */

public class Server {
    private Socket socket = null;
    private String message = "";
    private boolean connected = false;
    Server(){
        try {
            socket = IO.socket("http://192.168.1.25:3001");
        }catch (Exception e) {
            e.printStackTrace();
        }
        init();
    }
    void init(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //on connect then send connect message
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

                EventBus.getDefault().post(new ServerReplyEvent(message));
                EventBus.getDefault().post(new ServerConnectedEvent());
                connected = true;
                Log.d("SocketIO", "Message from Server " + message);
                Log.d("SocketIO", "Message from Server " + args.length);
//                        output.setText(message);
                //socket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("SocketIO", "disconnected");
            }

        });
        socket.connect();
    }
    void disconnect_server(){
        socket.disconnect();
    }
    void send_message(JSONObject data){
        //assuming the data is in json
        //emit to toy name
        if(connected){
            socket.emit("echo", data);
        }
    }

}
