package com.project.secertproject;
import android.util.Log;

import org.json.JSONObject;
/**
 * Created by Sonny on 5/13/2017.
 */
public class ToyManager {


    private String local_ip;
    private String toy_id;
    private String port;
    public String toy_name = "";

    ToyManager(){
    }

    //send the command to toy
    //if send was success then return true
    Boolean send_command(String command, int value){
        Boolean sucess = false;
        String url = "http://" + local_ip +":" + port + "/"+ command +"?v="+ value + "&t=" + toy_id;
        if(command.equals("RotateChange")){
            url = "http://" + local_ip +":" + port + "/"+ command + "?t=" + toy_id;
        }
        try{
            String output = new JsonTask().execute(url).get();
            Log.d(this.getClass().getName() + " output", output);
            JSONObject json_object = new JSONObject(output);
            if(json_object.length()!=0 && json_object.get("type") != "error"){


                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return  sucess;
    }
    //tries to gather information for local host and toy
    Boolean connect_toy(){
        Boolean connected = false;
        String url = "https://lovense.com/api/getLanToys";
        try{
            String output = new JsonTask().execute(url).get();
            Log.d(this.getClass().getName() + " output", output);
            JSONObject json_object = new JSONObject(output);
            //if output is empty we have a problem
            if(json_object.length()!=0){
                //get the details
                local_ip = json_object.keys().next();
                port = json_object.getJSONObject(local_ip).getString("port");
                toy_id = json_object.getJSONObject(local_ip).getJSONObject("toys").keys().next();
                toy_name = json_object.getJSONObject(local_ip).getJSONObject("toys").getJSONObject(toy_id).getString("name");
                if(toy_name.equals("nora") ){
                    toy_name = "max";
                }else if(toy_name.equals("max")){
                    toy_name = "nora";
                }
                Log.d(this.getClass().getName() + " local_ip", local_ip);
                Log.d(this.getClass().getName() + " port", port);
                Log.d(this.getClass().getName() + " toy_id", toy_id);
                Log.d(this.getClass().getName() + " toy_name", toy_name);
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return connected;
    }

}
