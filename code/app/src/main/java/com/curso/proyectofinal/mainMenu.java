package com.curso.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wladivalenzuela on 2017-06-23.
 */

public class mainMenu extends AppCompatActivity {

    EditText nickName, ipServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lo inicial base de cada actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        nickName = (EditText) findViewById(R.id.nickname);
        ipServer = (EditText) findViewById(R.id.ip_server);
        loadSettings();

    }

    public void btnNewGame(View v) {
        Intent game = new Intent(this, mainGame.class);
        String nickname = nickName.getText().toString();
        String IPServer = ipServer.getText().toString();

        game.putExtra("nick", nickname);
        game.putExtra("ipserver", IPServer);

        startActivity(game);    // Se inicia la actividad opciones
    }

    public void btnSaveSettings( View v ) {
        try {
            FileOutputStream fos = openFileOutput("playerSettings", MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);
            String nickname = nickName.getText().toString();
            String IPServer = ipServer.getText().toString();
            String toFile = nickname+"\n"+IPServer;
            dos.writeBytes(toFile);
            dos.flush();
            dos.close();
            Toast.makeText(this, "Settings Saved",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        try {
            InputStream is = openFileInput("playerSettings");

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            String text = new String(buffer);

            String[] fromFile = text.split("\n");
            String nickname = fromFile[0];
            String IPServer = fromFile[1];

            nickName.setText(nickname);
            ipServer.setText(IPServer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
