package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.JoyceK0.Olympic_Islands.component.Move;
import com.github.JoyceK0.Olympic_Islands.component.Physic;

public class PhysicMoveSystem extends IteratingSystem {

    private final Vector2 normalizedDirection = new Vector2();

    public PhysicMoveSystem() {
        super(Family.all(Physic.class, Move.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        Body body = Physic.MAPPER.get(entity).getBody();
        if(move.isRooted() || move.getDirection().isZero()) { // if no movements to move or character is frozen/disabled then return without performing any action
            body.setLinearVelocity(0f, 0f);
            return;
        }

        normalizedDirection.set(move.getDirection()).nor(); // to avoid moving faster in the diagonal directions, normalize the vector direction to unit of 1 (happens because x and y add up)
        body.setLinearVelocity(
            move.getMaxSpeed() * normalizedDirection.x, // deltaTime is the time between two frames, doesn't matter how many frames any computer has, speed value and change is set accordingly
            move.getMaxSpeed() * normalizedDirection.y
        );

    }
}
