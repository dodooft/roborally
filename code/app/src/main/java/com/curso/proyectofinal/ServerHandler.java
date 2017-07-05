package com.curso.proyectofinal;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;


/**
 * Created by jsoto on 14-06-17.
 */


public class ServerHandler {

    private sHandlerCallback callbacksServer = null;
    private rbServer server;
    private Integer SERVER_PORT = 5000;
    private String  SERVER_IP;

    private AppCompatActivity activity;
    private String current_nick;


    public ServerHandler(AppCompatActivity activity, String nick, String serverIP) {
        this.current_nick  = nick;
        this.activity      = activity;
        this.SERVER_IP     = serverIP;
    }

    private serverCallback sCallback = new serverCallback() {
        @Override
        public void onMessage(String message) {
            Log.v("Server", message);

            JSONObject object   = null;
            String cmd_name     = "";
            // Read cmd of message
            try {
                object = new JSONObject(message);
                cmd_name = object.optString("cmd", "err");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // return if no json object
            if (object == null) return;

            final JSONArray data;
            // Processe message
            switch (cmd_name) {
                // new player on game
                case "info_player":
                    try {
                        data = object.getJSONArray("value");

                        // Call on connect callback
                        if (callbacksServer != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        callbacksServer.onConnect(data.getInt(0));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                // new player on game
                case "new_player":
                    try {
                        data = object.getJSONArray("value");

                        if (callbacksServer != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        callbacksServer.onAddPlayer(data.getInt(0), data.getString(2));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                // del player on game
                case "del_player":
                    try {
                        data = object.getJSONArray("value");


                        // Call on disconnect player callback
                        if (callbacksServer != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        callbacksServer.onDelPlayer(data.getInt(0));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "start_game":
                    // Call start game callback
                    if (callbacksServer != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callbacksServer.onStartGame();
                            }
                        });
                    }
                    break;

                case "new_turn":
                    // Call start game callback
                    if (callbacksServer != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callbacksServer.onTurnMessage();
                            }
                        });
                    }
                    break;

                case "new_subturn":
                    // Call start game callback
                    if (callbacksServer != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callbacksServer.onSubTurnMessage();
                            }
                        });
                    }
                    break;

                case "new_subsubturn":
                    try {
                        data = object.getJSONArray("value");

                        // Call start game callback
                        if (callbacksServer != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        callbacksServer.onSubSubTurnMessage(data.getInt(0), data.getInt(1), data.getInt(2));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // No correct message
                default: break;
            }

        }

        @Override
        public void onDisconnect() {
            if (callbacksServer != null) {
                callbacksServer.onDisconnect();
            }
        }
    };

    public boolean isConnected() {
        return server.isConnected();
    }


    public void connectToServer()
    {
        server = new rbServer(SERVER_IP, SERVER_PORT, current_nick);
        server.connectServer();

        server.inMessageCallback(sCallback);
    }

    private void sendCommandtoServer(String command, Object value) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", command);

            if (value != null) {
                object.put("value", value);
            }

            server.sendMessage(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        sendCommandtoServer("start_game", null);
    }

    public void closeConnection() {
        sendCommandtoServer("disconnect", null);
    }

    enum ACK {
        TURN, SUBTURN, SUBSUBTURN, WIN, TIMEOUT_ORDER
    }

    public void sendACK(ACK type) {
        sendCommandtoServer("ack", type.toString());
    }

    public void sendCard(int value, int priority) {
        sendCommandtoServer("card", Arrays.toString(new int[]{value, priority}));
    }

    public void registerCallbacks(sHandlerCallback functions) {
        this.callbacksServer = functions;
    }

}
// prototype of callback
interface sHandlerCallback {
    void onTurnMessage();
    void onSubTurnMessage();
    void onSubSubTurnMessage(int user_id, int card_type, int priority);
    void onStartGame();
    void onConnect(int user_id);
    void onDisconnect();
    void onDelPlayer(int user_id);
    void onAddPlayer(int user_id, String nickname);
}