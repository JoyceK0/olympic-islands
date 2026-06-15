package com.github.JoyceK0.Olympic_Islands.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class Move implements Component { // the component class helps with the creation of component mappers for property management

    // this class declares the move class which contains key attributes for moving entities such as its speed, direction, and state
    // It also creates a new component mapper to manage this property for all entities

    public static final ComponentMapper<Move> MAPPER = ComponentMapper.getFor(Move.class);
    // create the component mapper for the Move properties of entities (definition of component mapper found in Physic component)

    // Attributes of the move class
    private float maxSpeed; // maximum speed of the moving entity
    private final Vector2 direction; // the direction of the entity, given as a coordinate vector (x, y)
    private boolean isRooted; // helps to keep the entity stuck in one place if you don't want it to move


    //Constructor
    public Move(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        this.direction = new Vector2(); // the direction of every entity is defined by the physics engine
    }


    // Accessors
    public float getMaxSpeed() {
        return maxSpeed;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public boolean isRooted() {
        return isRooted;
    }

    // Modifier for isRooted
    public void setRooted(boolean rooted) {
        this.isRooted = rooted; // set to true if you do not wish the entity with this property to move
    }


}
