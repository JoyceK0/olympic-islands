package com.github.JoyceK0.Olympic_Islands.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Physic implements Component { // the component class helps with the creation of component mappers for property management

    // The purpose of this component is to follow the ECS structure by creating a component mapper for the physics properties of entities in the game,
    // defined by the physics class which holds key variables for the physics engine such as the body and its position

    public static final ComponentMapper<Physic> MAPPER = ComponentMapper.getFor(Physic.class);
    // the component mapper in simple terms creates a list/array where the type is the component type of the entity which in this case is physics class,
    //  and the index of the array is the randomly assigned entity Id this mapping system reduces the time and memory allocation of resources for finding
    //  properties of entities, and instead of grouping entities by their properties, they group properties of entities in arrays

    // these are attributes of the physics class
    private final Body body; // this is an attribute of the physics class which keeps track of the entity's physical form in the physics world
    private final Vector2 prevPosition; // this is the attribute which defines the position of the entity in the game screen

    // Constructor which instantiates the key variables through parameters
    public Physic(Body body, Vector2 prevPosition) {
        this.body = body;
        this.prevPosition = prevPosition;
    }

    //Accessor methods

    public Body getBody() {
        return body;
    }

    public Vector2 getPrevPosition() {
        return prevPosition;
    }


}
