package com.curso.proyectofinal;

import android.widget.ImageView;

/**
 * Created by JP on 21-06-2017.
 */

public class Card {
    private ImageView myImageView; // ImageView asociado a la carta
    private int	priority; // Prioridad de la carta (servidor resuelve orden de cada subturno en base a la prioridad)
    private int	type; //Tipo de la carta (MOVE1, MOVE2, TURNL, TURNR, etc.)
    private int	position; //Posición de la carta dentro de la mano (orden de ejecución de cada carta)

    public Card (int priority, int type, int position) {
        this.priority = priority;
        this.type = type;
        this.position = position;
    }

    // Setters
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public void setImageView (ImageView imageview) {
        this.myImageView = imageview;
    }
    // Getters
    public int getPriority() {
        return this.priority;
    }
    public int getType() {
        return this.type;
    }
    public int getPosition() {
        return this.position;
    }
    public ImageView getImageView() {
        return this.myImageView;
    }
}
