package com.curso.proyectofinal;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by jsoto on 16-06-17.
 */

public class rbServer {
    private Integer         serverPort;
    private String          serverIP;
    private socketThread    sRun;
    private Thread          sThread;

    public rbServer(String serverIP, Integer serverPort, String nickname) {
        this.setServerIP(serverIP);
        this.setServerPort(serverPort);

        sRun    = new socketThread(nickname);
        sThread = new Thread(sRun);
    }

    public boolean isConnected() {
        return sThread.isAlive();
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public void connectServer() {
        if ( !sThread.isAlive() ) sThread.start();
    }

    public void disconnectServer() {
        if ( !sThread.isAlive() ) sRun.disconnect();
    }

    public void sendMessage(String message) {
        sRun.send(message);
    }

    public void inMessageCallback(serverCallback scallback) {
        sRun.registerCallback(scallback);
    }

    // socket input and output run in other thread
    class socketThread implements Runnable {
        private Socket          socket;
        private OutputStream    socketOutput;
        private BufferedReader  socketInput;
        private serverCallback  scallback = null;
        private String          nickname = "";

        public socketThread(String nick) {
            this.nickname = nick;
        }

        @Override
        public void run() {
            String message;

            // connect to server and wait for messages
            try {
                this.socket         = new Socket(rbServer.this.serverIP, rbServer.this.serverPort);
                this.socketOutput   = socket.getOutputStream();
                this.socketInput    = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Send nickname to server;
                JSONObject object = new JSONObject();

                try {
                    object.put("nickname", this.nickname);
                    // Send
                    this.send(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // call callback function in a new message
                while ((message = socketInput.readLine()) != null) {
                    if (this.scallback != null) {
                        this.scallback.onMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (this.scallback != null) {
                this.scallback.onDisconnect();
            }
        }
        // create a new async task to send data
        public void send(String message) {
            (new sendDataTask()).execute(message);
        }
        // close socket
        public void disconnect() {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // register callback for input messages
        public void registerCallback(serverCallback scallback) {
            this.scallback = scallback;
        }
        // Async task to send data in other thread
        private class sendDataTask extends AsyncTask<String, String, String> {
            protected String doInBackground (String... data) {
                try {
                    socketOutput.write(data[0].getBytes());
                    return "ok";
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.toString();
                }
            }
        }
    }
}

// prototype of callback
interface serverCallback {
    void onMessage(String message);
    void onDisconnect();
}
