package io.github.anthonyclemens.utils;

import org.newdawn.slick.util.Log;

public class ClientHandler {
    private final String ipAddress;
    private final int port;
    private final String userName;

    public ClientHandler(String ipAddress, int port, String userName){
        this.ipAddress = ipAddress;
        this.port = port;
        this.userName = userName;
    }

    public void connect(){
        try {
            Log.debug("Connecting to "+this.ipAddress+":"+this.port+" as "+this.userName);
            //Connection to server
        } catch (Exception e) {
            //Connection error
        }
        Log.debug("Connected successfully.");
    }

    public int getSeed(){
        return 123456;
    }


}