package com.curso.proyectofinal;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ViewSwitcher;

import java.util.Locale;
import java.util.TimerTask;

/**
 * Created by jsoto on 01-07-17.
 */

public class mainGame extends AppCompatActivity {
    private     ServerHandler   sHandler = null;
    private     Lobby           lobbyManager;
    private     Board           boardManager;
    private     Logic           logicManager;

    private     ViewSwitcher    viewSwitch;
    private     String          myUserName;
    private     MediaPlayer     mainSong;
    public      Player[]        Players;
    private     int             myID;
    private     TimerTask       orderTimer;
    private     int             boardSize;
    private     Button          infoLabel;
    private     int             turnNumber;

    public      musicplayer     myMusicPlayer;

    private int[] soundRobotResource, soundCardResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lo inicial base de cada actividadad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game);


        this.myUserName         = getIntent().getStringExtra("nick");       // username de este jugador
        Players                 = new Player[Constant.MAX_PLAYER_NUMBER];   // vector de Player de largo fijo
        this.boardSize          = 25;
        this.turnNumber         = 0;

        // Server
        String IPServer = getIntent().getStringExtra("ipserver");
        if (sHandler == null) {
            Log.v("New Server", "ASD");
            sHandler        = new ServerHandler(this, this.myUserName, IPServer);
            sHandler.registerCallbacks(sCallback);
            sHandler.connectToServer(); // flujo del programa continua en sHandlerCallback
        }
        // Logic
        logicManager = new Logic(getBaseContext(), Players);
        logicManager.registerCallback(lCallback);
        // Board
        boardManager = new Board(getBaseContext(), getWindowManager().getDefaultDisplay().getWidth(), Players);
        boardManager.registerCallback(bCallback);
        // Lobby
        lobbyManager = new Lobby(getBaseContext());

        viewSwitch              = (ViewSwitcher)        findViewById(R.id.viewSwitch);
        infoLabel               = (Button)              boardManager.getLayout().findViewById(R.id.infoLabel);

        viewSwitch.addView(lobbyManager.getLayout());
        viewSwitch.addView(boardManager.getLayout());



        myMusicPlayer = new musicplayer(getBaseContext());
        myMusicPlayer.setMainSong(this,R.raw.mainsong);
        myMusicPlayer.startMainSong();
        soundRobotResource = guiMediaPlayerSetting(6,"motor");
        soundCardResource = guiMediaPlayerSetting(4,"card");
        myMusicPlayer.setRobotEffect(this,6,this.soundRobotResource);
        myMusicPlayer.setCardEffect(this,4,this.soundCardResource);
    }

    void sendMessageToServer(int messageID, int cardValue, int cardPriority) {
        // tipos posibles en serverHandler: TURN, SUBTURN, SUBSUBTURN, TIMEOUT_ORDER, WIN

        Log.v("CMD", "onSendMesage 1");

        if (!sHandler.isConnected()) return;
        Log.v("CMD", "onSendMesage 2");

        switch (messageID) {
            case (Constant.MESSAGE_SEND_ENDOFTURN):
                this.sHandler.sendACK(ServerHandler.ACK.TURN);
                break;
            case (Constant.MESSAGE_SEND_ENDOFSUBTURN):
                this.sHandler.sendACK(ServerHandler.ACK.SUBTURN);
                break;
            case (Constant.MESSAGE_SEND_ENDOFSUBSUBTURN):
                this.sHandler.sendACK(ServerHandler.ACK.SUBSUBTURN);
                break;
            case (Constant.MESSAGE_SEND_CARD):
                this.sHandler.sendCard(cardValue, cardPriority) ;
                break;
            case (Constant.MESSAGE_SEND_WIN):
                this.sHandler.sendACK(ServerHandler.ACK.WIN);
                break;
            case (Constant.MESSAGE_TIMEOUT_ORDER):
                this.sHandler.sendACK(ServerHandler.ACK.TIMEOUT_ORDER);
                break;
            default:
                break;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boardManager.guiDrawLife(Players[myID]);
            }
        });
    }

    /**********************************************************/
    /****************     Order Timer    **********************/
    /**********************************************************/

    private class waitForOrderCardsTask extends AsyncTask<Integer, Integer, Integer> {
        protected Integer doInBackground(Integer... args) {
            int timeOut = args[0] / 1000;

            while( timeOut > 0 ) {
                publishProgress(timeOut);
                timeOut = timeOut - 1;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
            infoLabel.setText(progress[0] + " s restantes");
        }

        protected void onPostExecute(Integer result) {
            boardManager.guiSetActiveCards(false, Players[myID]); // desactivr elección de cartas
            sendMessageToServer(Constant.MESSAGE_TIMEOUT_ORDER, 0, 0);
            // ir a sHandlerCallback para seguir flujo de programa
            // increase turn counter
            turnNumber = turnNumber + 1;
            infoLabel.setText("Turno " + turnNumber);
        }
    }


    /**********************************************************/
    /*******************     Music    *************************/
    /**********************************************************/

    public int[] guiMediaPlayerSetting(int numberEffects, String name) {
        int [] inputResource = new int[numberEffects];
        for (int i = 0; i < numberEffects; ++i) {
            String resName = String.format(Locale.US, name+"%d", i + 1);
            int resID = this.getResources().getIdentifier(resName, "raw", this.getPackageName());
            inputResource[i] = resID;
        }
        return inputResource;
    }

    /**********************************************************/
    /*******************  BTN Layout  *************************/
    /**********************************************************/

    // Funciones de botones en layout //

    public void btnZoomIn( View view ){
        boardManager.zoomAction(+20);
    }

    public void btnZoomOut( View view ){
        boardManager.zoomAction(-20);
    }

    public void btnHideLayout(View view){
        // Toggle visibility
        boardManager.setVisible(!boardManager.isVisible());
    }

    /**********************************************************/
    /**                Lobby button functions                **/
    /**********************************************************/

    public void btnBackToMenu(View view) {
        if (sHandler.isConnected()) {
            sHandler.closeConnection();
        }
        this.finish();
    }

    public void btnStartGame(View view) {
        if (!sHandler.isConnected()) return;

        sHandler.startGame();
    }


    /**********************************************************/
    /**                     Board callbacks                  **/
    /**********************************************************/

    private boardCallback bCallback = new boardCallback() {
        @Override
        public void playRobot(int type) {
            myMusicPlayer.playRobotEffect(type);
        }
        @Override
        public void playCard(int type) {
            myMusicPlayer.playCardEffect(type);
        }
    };

    /**********************************************************/
    /**                     Logic callbacks                  **/
    /**********************************************************/

    private logicCallback lCallback = new logicCallback() {
        @Override
        public void messageToServer(int type) {
            sendMessageToServer(type, 0, 0);
        }

        @Override
        public void finishGame(int status) {
            if (status == Constant.GAME_STATUS_WIN) {
                boardManager.showNotify("Fin de juego", null, -1, -1, "Has ganado");
            }
            else {
                boardManager.showNotify("Fin de juego", null, -1, -1, "Has perdido");
            }
        }

        @Override
        public void attenuateCard(final int cardIndex, final boolean attenuate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boardManager.attenuateCard(cardIndex, attenuate);
                }
            });
        }
    };

    /**********************************************************/
    /**                     Server callbacks                 **/
    /**********************************************************/

    private sHandlerCallback sCallback = new sHandlerCallback() {
        @Override
        public void onTurnMessage() { // inicio de turno
            Log.v("CMD", "Turn");
            logicManager.createHand();
            boardManager.guiLoadHand(Players[myID]);
            boardManager.guiSetActiveCards(true, Players[myID]); // activar elección de cartas
            new waitForOrderCardsTask().execute(Constant.TIMER_SECONDS);// lanzar timer para que le jugador ordene sus cartas


            boardManager.showNotify("Nuevo Turno", null, -1, -1, "Programa mano para\nel próximo turno");
        }

        @Override
        public void onSubTurnMessage() { // inicio de sub turno
            Log.v("CMD", "SubTurn");

            // A. Reveal Program Cards
            Card currentCard = Players[myID].getNextCard();
            sendMessageToServer(Constant.MESSAGE_SEND_CARD, currentCard.getType(), currentCard.getPriority()); // agregar argumentos
        }

        @Override
        public void onSubSubTurnMessage(int user_id, int card_type, int priority) {
            Log.v("CMD", "SubSubTurn " + user_id + " " + card_type+ " " + priority);
            boardManager.guiShowTurnPopup(Players[user_id], card_type, priority); // mostrar carta a jugar en GUI
            // B. Robots Move
            logicManager.robotsMove(Players[user_id], card_type);
            boardManager.guiDrawMovement(Players[user_id]);

            logicManager.responseToServer(Players[user_id]);
        }

        @Override
        public void onStartGame() {
            Log.v("CMD", "StartGame");
            boardManager.guiGenerateLayout();
            boardManager.guiDrawAllPlayers();
            logicManager.setInformacion(boardManager.getInformationMat());
            viewSwitch.showNext();
        }

        @Override
        public void onConnect(int user_id) {
            myID = user_id;

            lobbyManager.setPlayerData(user_id, myUserName);
            logicManager.setPlayerData(user_id, myUserName);
            boardManager.setPlayerData(user_id, myUserName);

            Log.v("CMD", "Connected");
        }

        @Override
        public void onDisconnect() {
            Log.v("CMD", "Disconnect");
        }

        @Override
        public void onDelPlayer(int user_id) {
            lobbyManager.delPlayer(user_id);
            Log.v("CMD", "Delete user");
            logicManager.deacreasePlayerNum();
            boardManager.deacreasePlayerNum();
        }

        @Override
        public void onAddPlayer(int user_id, String nickname) {
            lobbyManager.newPlayer(user_id, nickname);
            logicManager.addNewPlayer(user_id, nickname);
            logicManager.increasePlayerNum();
            boardManager.increasePlayerNum();
        }
    };

    /**********************************************************/
    /**                       APP functions                  **/
    /**********************************************************/

    @Override
    protected void onPause(){    // Se atiende la pausa, pausando la musica de fondo
        myMusicPlayer.pauseMainSong();
        super.onPause();
    }
    @Override
    protected void onResume(){  // Se atiende resume, reanudando la musica de fondo
        myMusicPlayer.startMainSong();
        super.onResume();
    }
    @Override
    protected void onStart(){   // Se atiende el comienzo, comenzando la musica de fondo desde el principio
        myMusicPlayer.startMainSong();
        super.onStart();
    }
    @Override
    protected void onRestart(){ // Se atiende el recomienzo, reanudando la musica de fondo desde cero
        myMusicPlayer.startMainSong();
        super.onRestart();
    }
    @Override
    protected void onDestroy(){ // Se atiende la finalización, deteniendo y liberando la musica de fondo
        myMusicPlayer.closeAllSounds();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (sHandler.isConnected()) {
            sHandler.closeConnection();
        }

        this.finish();
    }
}
