package com.curso.proyectofinal;

/**
 * Created by JP on 21-06-2017.
 */

public class Action {
    //ACTIONS	type; // Tipo de acción (MOVE; PUSH, FALL, DIE; etc)
    private int	type; // Tipo de acción (MOVE; PUSH, FALL, DIE; etc)
    private Position position; // Casillero donde se lleva a cabo la acción

    public Action (int type, Position position) {
        this.type = type;
        this.position = position;
    }

    // Setters
    void setType (int type) {
        this.type = type;
    }
    void setPosition (Position position) {
        this.position = position;
    }

    // Getters
    int getType () {
        return this.type;
    }
    Position getPosition () {
        return this.position;
    }
}
