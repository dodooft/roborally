package com.curso.proyectofinal;

/**
 * Created by JP on 21-06-2017.
 */

public class Player {
    private Ptoken myToken; // Objeto tipo Token que representa la ficha del jugador
    private Hand hand = null; // Objeto tipo Hand que representa la mano de cartas del jugador
    private int	life; // Puntos de vida del jugador
    private int	userId; // ID del jugador (se pregunta al servidor)
    private int	gameId; // ID de la partida (se pregunta al servidor)
    private int	userStatus; // Estado del jugador (WAITING; PREPARING; READY)
    private String userName;

    public Player(int userID, String userName) {
        this.life = Constant.PLAYER_INITIAL_LIFE;
        this.userId = userID;
        this.gameId = -1;
        this.userStatus = -1;
        this.userName = userName;

        this.hand = new Hand();
        this.myToken = new Ptoken();
    }

    // Setters
    public void setLife(int life) {
        this.life = life;
    }
    public void setUserId (int id) {
        this.userId = id;
    }
    public void setGameId (int id) {
        this.gameId = id;
    }
    public void setUserStatus (int status) {
        this.userStatus = status;
    }

    // Getters

    public String getUserName() {
        return userName;
    }

    public int getLife() {
        return this.life;
    }
    public int getUserId () {
        return this.userId;
    }
    public int getGameId () {
        return this.gameId;
    }
    public int getUserStatus () {
        return this.userStatus;
    }
    public Ptoken getToken () {
        return this.myToken;
    }
    public Card getNextCard () {
        return this.hand.getNextCard();
    }
    public Hand getHand () { return this.hand; }

    public boolean diminishLife() {
        this.life--;
        if (this.life == 0) {
            this.life = Constant.PLAYER_INITIAL_LIFE;
            return false;
        }
        return true;
    }
    public void increaseLife() {
        this.life++;
    }
}