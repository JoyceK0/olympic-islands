package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.JoyceK0.Olympic_Islands.component.Move;
import com.github.JoyceK0.Olympic_Islands.component.Physic;

public class PhysicMoveSystem extends IteratingSystem {

    // This class manages all movement incoming from the move system and applies world physics to it
    // This extends the iterating system, which essentially helps narrow down the affected entities
    // for the system rather than looping through the entire entity mapper to grab the required entities

    private final Vector2 normalizedDirection = new Vector2();
    // we need the normalized direction vector for character movement, explained below on application

    public PhysicMoveSystem() {
        super(Family.all(Physic.class, Move.class).get());
        // get all entities with the physic and move class attributes
    }

    // Customize how to process the entities, being moved in the physic world
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity); // get the move class for the entity
        Body body = Physic.MAPPER.get(entity).getBody(); // get the body of the entity through the physic class
        if(move.isRooted() || move.getDirection().isZero()) { // if no movements to move or character is frozen/disabled then return without performing any action
            body.setLinearVelocity(0f, 0f); // freeze character movement
            return;
        }

        normalizedDirection.set(move.getDirection()).nor(); // to avoid moving faster in the diagonal directions, normalize the vector direction to unit of 1
        // (happens because x and y vectors add up in the diagonal direction, causing the character to move faster)

        //set the linear velocity of the physic body in the physic world for internal calculation
        body.setLinearVelocity(
            move.getMaxSpeed() * normalizedDirection.x,
            move.getMaxSpeed() * normalizedDirection.y
        );

    }
}
