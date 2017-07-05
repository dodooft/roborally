package com.curso.proyectofinal;

/**
 * Created by JP on 22-06-2017.
 */

public class Constant {
    public static final int NCARDS_PER_HAND = 9;
    public static final int NCARDS_PER_HAND_PLAYABLE = 5;
    public static final int PLAYER_INITIAL_LIFE = NCARDS_PER_HAND;
    public static final int MAX_PLAYER_NUMBER = 4;

    public static final int CARD_MOVE_1 = 1;
    public static final int CARD_MOVE_2 = 2;
    public static final int CARD_MOVE_3 = 3;
    public static final int CARD_BACK_UP = 4;
    public static final int CARD_ROTATE_180 = 5;
    public static final int CARD_ROTATE_LEFT = 6;
    public static final int CARD_ROTATE_RIGHT = 7;
    public static final int CARD_NONE = 8;
    public static final int CARD_NTYPES = 7;
    public static final int CARD_MAX_PRIORITY = 100;

    public static final int ACTION_NONE = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_ROTATE = 3;
    public static final int ACTION_FALL = 4;
    public static final int ACTION_PUSH = 5;
    public static final int ACTION_WIN = 6;
    public static final int ACTION_DESTROY = 7;

    public static final int TOKEN_ORIENTATION_U = 1;
    public static final int TOKEN_ORIENTATION_R = 2;
    public static final int TOKEN_ORIENTATION_D = 3;
    public static final int TOKEN_ORIENTATION_L = 4;

    public static final int TIMER_SECONDS = 30000;

    public static final int MESSAGE_TIMEOUT_ORDER = 3000;
    public static final int MESSAGE_SEND_CARD = 3001;
    public static final int MESSAGE_SEND_ENDOFSUBSUBTURN = 3002;
    public static final int MESSAGE_SEND_ENDOFSUBTURN = 3003;
    public static final int MESSAGE_SEND_ENDOFTURN = 3004;
    public static final int MESSAGE_SEND_WIN = 3005;

    public static final int GAME_STATUS_WIN = 4000;
    public static final int GAME_STATUS_LOSE = 4001;

    public static final int SQUARE_TYPE_EMPTY = 0;
    public static final int SQUARE_TYPE_WALL = 1;
    public static final int SQUARE_TYPE_HOLE = 2;
    public static final int SQUARE_TYPE_START = 3;
    public static final int SQUARE_TYPE_FINISH = 4;
    public static final int SQUARE_TYPE_TRANSPORT_BELT_R = 5;
    public static final int SQUARE_TYPE_TRANSPORT_BELT_L = 6;
    public static final int SQUARE_TYPE_TRANSPORT_BELT_U = 7;
    public static final int SQUARE_TYPE_TRANSPORT_BELT_D = 8;

    public static final int SQUARETABLE_DIMENSION = 11;

    public static final int SQUARETABLE_STARTX = 0;
    public static final int SQUARETABLE_STARTY = 0;

}
