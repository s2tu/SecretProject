package com.project.secertproject;
import android.util.Log;

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

    Server(){
        try {
            socket = IO.socket("http://10.15.202.148:3001");
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

                for(int i=0;i < args.length; i++){
                    message = message + args[i] + " ";
                }

                EventBus.getDefault().post(new ServerReplyEvent(message));
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
    public String get_message(){
        return  this.message;
    }

}
