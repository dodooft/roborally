package com.curso.proyectofinal;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JP on 21-06-2017.
 */

public class Logic {
    private int cardsPlayed;
    private int playersPlayed;
    private Player[]       Players;
    private int myID;
    private String myUserName;
    int informacion[][];                            // Matriz con la información de cada celda del tablero (solo para carga inicial en crear matriz)
    private int playerNumber;
    private logicCallback callback = null;


    public Logic(Context context, Player[] players) {
        this.Players            = players;
        this.playerNumber       = 0;
        this.cardsPlayed        = 0;                                        // numero de cartas juagas (por cada jugador) -> subturnos
        this.playersPlayed      = 0;                                        // numero de cartas jugadas por subsubturno (= numero de jugadores)
    }

    public void setInformacion(int[][] informacion) {
        this.informacion    = informacion;
    }

    public void addNewPlayer(int userID, String userName) {
        Players[userID] = new Player(userID, userName);
    }

    public void setPlayerData (int userID, String myUserName) {
        this.myID = userID;
        this.myUserName = myUserName;

        Players[this.myID] = new Player(this.myID, this.myUserName);
    }

    public void increasePlayerNum() {
        this.playerNumber = this.playerNumber + 1;
    }

    public void deacreasePlayerNum() {
        this.playerNumber = this.playerNumber - 1;
    }

    public void registerCallback(logicCallback callback) {
        this.callback = callback;
    }


    // Inicio de turno, robar cartas
    // este método se llama desde sHandlerCallback
    void createHand() {
        // se asume que this.Players es una lista de jugadores
        this.Players[this.myID].getHand().drawHand();
        // ir a timerCallback para seguir flujo de programa
    }

    void robotsMove (Player player, int movement) { // executar carta segun tipo
        Action historic;

        switch (movement)  {
            case Constant.CARD_MOVE_1:
                moveToken(player, 1);
                Log.d("JP","TIPO CARTA: CARD_MOVE1");
                break;
            case Constant.CARD_MOVE_2:
                Log.d("JP","TIPO CARTA: CARD_MOVE2");
                //moveToken(player, 2);
                historic = new Action(Constant.ACTION_NONE,new Position(-1,-1));
                for (int i = 0; i < 2; i++) {
                    Log.d("JP","Iterando para mover. i = " + i);
                    moveToken(player, 1);
                    if (player.getToken().getAction().getType() == Constant.ACTION_FALL)
                        break;
                    else if (player.getToken().getAction().getType() == Constant.ACTION_MOVE) {
                        Log.d("JP","Moviendo un step y guardando histórico!");
                        historic = new Action(player.getToken().getAction().getType(), player.getToken().getAction().getPosition());
                    }
                    else if (player.getToken().getAction().getType() == Constant.ACTION_NONE) {
                        Log.d("JP","Al parecer me encontré con un muro");
                        if (historic.getType() == Constant.ACTION_MOVE) {
                            Log.d("JP","Movimiento anterior fue un MOVE. Recuperando histórico.");
                            player.getToken().setAction(historic.getType(),historic.getPosition());
                        }
                    }
                }
                break;
            case Constant.CARD_MOVE_3:
                Log.d("JP","TIPO CARTA: CARD_MOVE3");
                //moveToken(player, 3);
                historic = new Action(Constant.ACTION_NONE,new Position(-1,-1));
                for (int i = 0; i < 3; i++) {
                    Log.d("JP","Iterando para mover. i = " + i);
                    moveToken(player, 1);
                    if (player.getToken().getAction().getType() == Constant.ACTION_FALL)
                        break;
                    else if (player.getToken().getAction().getType() == Constant.ACTION_MOVE) {
                        Log.d("JP","Moviendo un step y guardando histórico!");
                        historic = new Action(player.getToken().getAction().getType(), player.getToken().getAction().getPosition());
                    }
                    else if (player.getToken().getAction().getType() == Constant.ACTION_NONE) {
                        Log.d("JP","Al parecer me encontré con un muro");
                        if (historic.getType() == Constant.ACTION_MOVE) {
                            Log.d("JP","Movimiento anterior fue un MOVE. Recuperando histórico.");
                            player.getToken().setAction(historic.getType(),historic.getPosition());
                        }
                    }
                }
                break;
            case Constant.CARD_BACK_UP:
                Log.d("JP","TIPO CARTA: CARD_BAKCUP");
                moveToken(player, -1);
                break;
            case Constant.CARD_ROTATE_180:
                Log.d("JP","TIPO CARTA: CARD_ROTATE180");
                rotateToken(player, -2);
                break;
            case Constant.CARD_ROTATE_LEFT:
                Log.d("JP","TIPO CARTA: CARD_ROTATELEFT");
                rotateToken(player, -1);
                break;
            case Constant.CARD_ROTATE_RIGHT:
                Log.d("JP","TIPO CARTA: CARD_ROTATERIGHT");
                rotateToken(player, 1);
                break;
            case Constant.CARD_NONE:
                Log.d("JP","TIPO CARTA: CARD_NONE");
                player.getToken().setAction(Constant.ACTION_NONE, player.getToken().getCurrentPosition());
                break;
            default:
                Log.d("JP","DEFULT");
                player.getToken().setAction(Constant.ACTION_NONE, player.getToken().getCurrentPosition());
                break;
        }
    }

    boolean moveToken(Player player, int steps) {
        Ptoken thisToken = player.getToken();
        int newX, newY;
        Log.d("JP","Mover token en " + steps + "steps");
        // calcular nueva posición en base a la carta jugada
        switch (thisToken.getOrientation()) {
            case (Constant.TOKEN_ORIENTATION_U):
                Log.d("JP","ORIENTACION U");
                newX = thisToken.getCurrentPosition().getX();
                newY = thisToken.getCurrentPosition().getY() - steps;
                // subir por el tablero = disminuir posiciÃ³n y
                break;
            case (Constant.TOKEN_ORIENTATION_D): // bajar por el tablero = aumentar posiciÃ³n y
                Log.d("JP","ORIENTACION D");
                newX = thisToken.getCurrentPosition().getX();
                newY = thisToken.getCurrentPosition().getY() + steps;
                break;
            case (Constant.TOKEN_ORIENTATION_L): // ir hacia la izquierda = disminuir x
                Log.d("JP","ORIENTACION L");
                newX = thisToken.getCurrentPosition().getX() - steps;
                newY = thisToken.getCurrentPosition().getY();
                break;
            case (Constant.TOKEN_ORIENTATION_R): // ir hacia la derecha = aumentar x
                Log.d("JP","ORIENTACION R");
                newX = thisToken.getCurrentPosition().getX() + steps;
                newY = thisToken.getCurrentPosition().getY();
                break;
            default:
                Log.d("JP","ORIENTACION DEFAULT");
                newX = thisToken.getCurrentPosition().getX();
                newY = thisToken.getCurrentPosition().getY();
        }

        Log.d("JP","New X: " + newX + "; New Y: " + newY);
        // ver qué hay en el tablero y resolver
        int squareType;
        if (newX >= Constant.SQUARETABLE_DIMENSION || newX < 0)
            squareType = Constant.SQUARE_TYPE_WALL;
        else if (newY >= Constant.SQUARETABLE_DIMENSION || newY < 0)
            squareType = Constant.SQUARE_TYPE_WALL;
        else
            //squareType = informacion[newX][newY];
            squareType = informacion[newY][newX]; // filas, columnas

        switch(squareType) {
            case (Constant.SQUARE_TYPE_HOLE):
                Log.d("JP","SQUARETYPE HOLE");
                thisToken.setCurrentPosition(Constant.SQUARETABLE_STARTX,Constant.SQUARETABLE_STARTY);
                thisToken.setAction(Constant.ACTION_FALL, new Position(newX, newY));
                player.diminishLife(); // si llega a cero, se maneja dentro
                break;
            case (Constant.SQUARE_TYPE_START):
                Log.d("JP","SQUARETYPE START");
                thisToken.setCurrentPosition(newX,newY);
                thisToken.setAction(Constant.ACTION_NONE, new Position(newX, newY));
                // GANAR UNA VIDA
                //players[player].increaseLife();
                break;
            case (Constant.SQUARE_TYPE_WALL):
                Log.d("JP","SQUARETYPE WALL");
                newX = thisToken.getCurrentPosition().getX();
                newY = thisToken.getCurrentPosition().getY();
                thisToken.setCurrentPosition(newX,newY);
                thisToken.setAction(Constant.ACTION_NONE, new Position(newX, newY));
                return false;
            case (Constant.SQUARE_TYPE_FINISH):
                Log.d("JP","SQUARETYPE FINISH");
                thisToken.setCurrentPosition(newX,newY);

                thisToken.setAction(Constant.ACTION_WIN, new Position(newX, newY));
                break;

            default: // TODO: Implement others
            case (Constant.SQUARE_TYPE_EMPTY):
                Log.d("JP","SQUARETYPE EMPTY");
                // ver si hay otra ficha en ese espacio
                int pX, pY, i;
                boolean robotConflict = false;

                for ( i = 0; i < Players.length; i++) {
                    if ( Players[i] != null ) {
                        pX = Players[i].getToken().getCurrentPosition().getX();
                        pY = Players[i].getToken().getCurrentPosition().getY();
                        if (pX == newX && pY == newY) {
                            robotConflict = true;
                            break;
                        }
                    }
                }
                if (robotConflict) {
                    Log.d("JP","&&& Hay un conflicto en X = "+ newX + ", Y = " + newY);
                    if (moveToken(Players[i], thisToken.getOrientation())) {
                        // se pudo empujar al robot, por lo tanto quedarse con la nueva posición
                        thisToken.setCurrentPosition(newX,newY);
                        thisToken.setAction(Constant.ACTION_PUSH, new Position(newX, newY));
                    }
                    else {
                        // no se pudo empujar al robot que ocupaba el casiller orque había un muro o algo
                        newX = thisToken.getCurrentPosition().getX();
                        newY = thisToken.getCurrentPosition().getY();
                        thisToken.setCurrentPosition(newX,newY);
                        thisToken.setAction(Constant.ACTION_NONE, new Position(newX, newY));
                    }
                }
                else {// no hay conflicto de robotos ocupando la posición, mover con confianza!
                    Log.d("JP","El espacio X = " + newX + ", Y = " + newY + " está desocupado, moviendo!");
                    thisToken.setCurrentPosition(newX, newY);
                    thisToken.setAction(Constant.ACTION_MOVE, new Position(newX, newY));
                }
                break;
        }
        return true;
    }
    void rotateToken (Player player, int steps) {
        Log.d("JP","Rotando token en " + steps + "steps");
        player.getToken().rotateToken(steps);
        player.getToken().setAction(Constant.ACTION_ROTATE, player.getToken().getCurrentPosition());
    }

    void resolveSubTurn () {
        // C. Board Elements Move
        boardsElementsMove();
        // D. Lasers Fire
        lasersFire(); // solo lasers de robots implementados
        // E. Touch Checkpoints
        touchCheckpoints(); // no implementado
    }

    void boardsElementsMove () {
    }

    void lasersFire () {

        for (int i = 0; i < Players.length; i++) {
            if ( Players[i] != null ) {
                // buscar robots en la vecindad y dependiendo de las orientaciones, disparar laser
                robotsFire(Players[i]);
                // buscar lasers del tablero en la vecindad
                tableLasersFire(Players[i]);
            }
        }
    }

    private void robotsFire(Player thisPlayer) {
        for (int i = 0; i < Players.length; i++) {
            if ( Players[i] != null ) {
                if (isTargetRobot(thisPlayer, Players[i])) {
                    if (!Players[i].diminishLife()) {
                        Players[i].getToken().setCurrentPosition(Constant.SQUARETABLE_STARTX, Constant.SQUARETABLE_STARTY);
                        Players[i].getToken().setAction(Constant.ACTION_DESTROY, Players[i].getToken().getCurrentPosition());
                    }
                }
            }
        }
    }

    boolean isTargetRobot (Player thisPlayer, Player otherRobot) {
        boolean looking = false;

        switch (thisPlayer.getToken().getOrientation()) {
            case (Constant.TOKEN_ORIENTATION_U):
                looking = (thisPlayer.getToken().getCurrentPosition().getY() == otherRobot.getToken().getCurrentPosition().getY() - 1);
                break;
            case (Constant.TOKEN_ORIENTATION_D):
                looking = (thisPlayer.getToken().getCurrentPosition().getY() == otherRobot.getToken().getCurrentPosition().getY() + 1);
                break;
            case (Constant.TOKEN_ORIENTATION_L):
                looking = (thisPlayer.getToken().getCurrentPosition().getX() == otherRobot.getToken().getCurrentPosition().getX() - 1);
                break;
            case (Constant.TOKEN_ORIENTATION_R):
                looking = (thisPlayer.getToken().getCurrentPosition().getX() == otherRobot.getToken().getCurrentPosition().getX() - 1);
                break;
            default:
                break;
        }
        return looking;
    }

    void tableLasersFire (Player thisPLayer) {

    }
    void touchCheckpoints() {
    }

    void finishGame (int status) {
        if (this.callback != null) {
            callback.finishGame(status);
        }
    }

    public void responseToServer(final Player player) {
        // TODO: Mejorar
        final finishRunnable runn = new finishRunnable (player);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runn.run();
                Log.v("CMD", "Timer send response");
            }
        };
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, 5000);
        // ir a sHandlerCallback para seguir flujo de programa
    }

    private class finishRunnable implements Runnable {
        Player player;
        public finishRunnable(Player player) {
            this.player = player;
        }
        @Override
        public void run() {

            if (player.getUserId() == myID) {
                callback.attenuateCard(cardsPlayed, true); // atenuar carta recién jugada}
                Log.d("PP","Carta " + cardsPlayed + " jugada");
            }
            Log.v("CMD", "Response ");
            if (player.getToken().getAction().getType() == Constant.ACTION_WIN) {
                if (player.getUserId() == myID) // 0 siempre es el usuario
                    finishGame(Constant.GAME_STATUS_WIN);
                else
                    finishGame(Constant.GAME_STATUS_LOSE);
            }

            playersPlayed++;
            if (playersPlayed < playerNumber) {

                if (player.getToken().getAction().getType() == Constant.ACTION_WIN) {
                    if (callback != null) {
                        callback.messageToServer(Constant.MESSAGE_SEND_WIN);
                    }
                }
                else {
                    if (callback != null) {
                        callback.messageToServer(Constant.MESSAGE_SEND_ENDOFSUBSUBTURN);;
                    }
                }
            }
            else {
                playersPlayed = 0;
                resolveSubTurn();
                cardsPlayed++;
                if (cardsPlayed < Constant.NCARDS_PER_HAND_PLAYABLE) {

                    if (callback != null) {
                        callback.messageToServer(Constant.MESSAGE_SEND_ENDOFSUBTURN);
                    }
                }
                else {
                    cardsPlayed = 0;
                    if (callback != null) {
                        callback.messageToServer(Constant.MESSAGE_SEND_ENDOFTURN);
                    }
                }
            }
        }
    }

}

// prototype of callback
interface logicCallback {
    void messageToServer(int type);
    void finishGame(int status);
    void attenuateCard(int cardIndex, boolean attenuate);
}
