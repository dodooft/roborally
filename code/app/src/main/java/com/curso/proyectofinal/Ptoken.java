package com.curso.proyectofinal;

import android.widget.ImageView;

/**
 * Created by JP on 21-06-2017.
 */

public class Ptoken {
    private Position previousPosition; // PosiciÃ³n anterior de la ficha
    private Position currentPosition; // PosiciÃ³n actual de la ficha
    private int orientation; // OrientaciÃ³n (direcciÃ³n donde se mueve actualmente)
    private Action myAction; // Objeto que modela la Ãºltima acciÃ³n realizada
    private ImageView tokenImage;

    public Ptoken (){
        this.previousPosition = new Position(-1,-1);
        this.currentPosition = new Position(0,0);
        this.orientation = Constant.TOKEN_ORIENTATION_D;
        this.myAction = new Action(0, new Position(0,0)); // prueba
    }

    // Setters
    void setCurrentPosition (int x, int y) {
        this.currentPosition.setX(x);
        this.currentPosition.setY(y);
    }
    void setPreviousPosition (int x, int y) {
        this.previousPosition.setX(x);
        this.previousPosition.setY(y);
    }
    void setOrientation (int orientation) {
        this.orientation = orientation;
    }

    void setTokenImage (ImageView imageView) { this.tokenImage = imageView; }

    void setAction (int type, Position position) {
        this.myAction.setType(type);
        this.myAction.setPosition(position);
    }

    void rotateToken(int steps) {
        this.orientation = this.orientation + steps;
        if (this.orientation > 4)
            this.orientation = this.orientation - 4;
        else if (this.orientation < 1)
            this.orientation = this.orientation + 4;
    }

    // Gettters
    Position getCurrentPosition () {
        return this.currentPosition;
    }
    Position getPreviousPosition () {
        return this.previousPosition;
    }
    int getOrientation () {
        return this.orientation;
    }
    ImageView getTokenImage () { return this.tokenImage; }
    Action getAction() {return this.myAction; }

}

class Position {
    private int x;
    private int y;

    // Setters
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    // Getters
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }


}
