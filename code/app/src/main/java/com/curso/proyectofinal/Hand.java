package com.curso.proyectofinal;
import android.util.Log;

import java.util.Random;
/**
 * Created by JP on 21-06-2017.
 */

public class Hand {
    private Card myCards[]; // Array de 7 objetos Card que representan la mano del jugador
    private int cardPointer;

    public Hand () {
        this.myCards = new Card[Constant.NCARDS_PER_HAND];
        drawHand();
    }

    public void drawHand () {
        // Quizás haya que cambiar esta forma por algo más inteligente
        // asignando prbabilidad de ocurrencia a cada tipo de carta
        this.cardPointer = -1;

        for ( int i = 0; i < Constant.NCARDS_PER_HAND ; i++ ) {

            myCards[i]= createRandomCard();
            myCards[i].setPosition(i);
        }
    }
    private Card createRandomCard () {
        Random randomGenerator = new Random();
        int priority = randomGenerator.nextInt(Constant.CARD_MAX_PRIORITY) + 1;
        //int type = randomGenerator.nextInt(Constant.CARD_NTYPES) + 1;
        int type = getRandomCardType();
        return new Card(priority, type, 0); // se le asigna posición 0 inicialmente
    }

    private int getRandomCardType () {
        Random randomGenerator = new Random();
        int prob = randomGenerator.nextInt(100) + 1;

        if (prob < 23)
            return Constant.CARD_MOVE_1;
        else if (prob < 23 + 15)
            return Constant.CARD_MOVE_2;
        else if (prob < 23 + 15 + 7)
            return Constant.CARD_MOVE_3;
        else if (prob < 23 + 15 + 7 + 7)
            return Constant.CARD_BACK_UP;
        else if (prob < 23 + 15 + 7 + 7 +21)
            return Constant.CARD_ROTATE_RIGHT;
        else if (prob < 23 + 15 + 7 + 7 + 21 + 21)
            return Constant.CARD_ROTATE_LEFT;
        else
            return Constant.CARD_ROTATE_180;
    }
    private int incrementPointer () {
        if (this.cardPointer < Constant.NCARDS_PER_HAND_PLAYABLE) {
            this.cardPointer++;
        }
        return this.cardPointer;
    }
    public Card getCard(int i) {
        return myCards[i];
    }
    public Card getNextCard () {
        printHand();
        incrementPointer();

        int i = 0;
        Log.d("JP","Inicio busqueda posición: pointer = " + this.cardPointer);
        for (i = 0; i < Constant.NCARDS_PER_HAND; i++) {
            Log.d("JP","Iterando: i = " + i + " ; postion = " + this.myCards[i].getPosition());
            if (this.myCards[i].getPosition() == this.cardPointer)
                return this.myCards[i];
        }

        return new Card(0,Constant.CARD_NONE,0);
    }

    public void printHand() {
        String myString = "printHand ## Position de cada carta: ";
        for (int i = 0; i < Constant.NCARDS_PER_HAND; i++) {
            myString = myString + myCards[i].getPosition() + ", ";
        }
        Log.d("JP",myString);
    }
}
