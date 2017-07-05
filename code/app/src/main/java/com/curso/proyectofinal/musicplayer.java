package com.curso.proyectofinal;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by wladivalenzuela on 2017-07-04.
 */

public class musicplayer {

    private MediaPlayer MainSong;
    private MediaPlayer[] RobotEffect;
    private MediaPlayer[] CardEffect;
    private int effectCardNumber,effectRobotNumber;

    public musicplayer(Context context){
        this.MainSong = new MediaPlayer();
    }

    public void setRobotEffect(Context context, int effectNumber, int[] effectIDs){
        this.effectRobotNumber = effectNumber;
        this.RobotEffect = new MediaPlayer[effectNumber];
        for ( int i = 0 ; i < effectNumber ; i++ ){
            Log.v("WV","Efecto n= " + i);
            this.RobotEffect[i] = new MediaPlayer();
            this.RobotEffect[i] = MediaPlayer.create( context , effectIDs[i]);
        }
    }

    public void setCardEffect(Context context, int effectNumber, int[] effectIDs){
        this.effectCardNumber = effectNumber;
        this.CardEffect = new MediaPlayer[effectNumber];
        for ( int i = 0 ; i < effectNumber ; i++ ){
            Log.v("WV","Efecto n= " + i);
            this.CardEffect[i] = new MediaPlayer();
            this.CardEffect[i] = MediaPlayer.create( context , effectIDs[i]);
        }
    }

    public void setMainSong(Context context, int id){
        this.MainSong = MediaPlayer.create( context , id);
        this.MainSong.setLooping(true);
    }


    public void startMainSong(){
        this.MainSong.seekTo(0);
        this.MainSong.start();
    }

    public void pauseMainSong(){
        this.MainSong.pause();
    }

    public void playRobotEffect(int effect){
        if ( effect < this.effectRobotNumber){
            this.RobotEffect[effect].seekTo(0);
            this.RobotEffect[effect].start();
        }
        else {
            this.RobotEffect[0].seekTo(0);
            this.RobotEffect[0].start();
        }
    }

    public void playCardEffect(int effect){
        if ( effect < this.effectCardNumber){
            this.CardEffect[effect].seekTo(0);
            this.CardEffect[effect].start();
        }
        else {
            this.CardEffect[0].seekTo(0);
            this.CardEffect[0].start();
        }
    }

    public void closeAllSounds(){

        for ( int i = 0 ; i < this.effectRobotNumber ; i++ ){
            if( this.RobotEffect[i].isPlaying()){
                this.RobotEffect[i].stop();
            }
            this.RobotEffect[i].release();
        }

        for ( int i = 0 ; i < this.effectCardNumber ; i++ ){
            if( this.CardEffect[i].isPlaying()){
                this.CardEffect[i].stop();
            }
            this.CardEffect[i].release();
        }

        if( this.MainSong.isPlaying()){
            this.MainSong.stop();
        }
        this.MainSong.release();
    }
}
