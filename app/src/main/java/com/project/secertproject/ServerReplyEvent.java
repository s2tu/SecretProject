package com.project.secertproject;

/**
 * Created by stu1 on 5/16/2017.
 */

public class ServerReplyEvent {
    String message;
    public ServerReplyEvent(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
