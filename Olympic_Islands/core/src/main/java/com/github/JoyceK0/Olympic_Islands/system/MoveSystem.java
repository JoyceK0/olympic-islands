package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.github.JoyceK0.Olympic_Islands.component.Move;
import com.github.JoyceK0.Olympic_Islands.component.Transform;

public class MoveSystem extends IteratingSystem {

    private final Vector2 normalizedDirection = new Vector2();

    public MoveSystem() {
        super(Family.all(Move.class, Transform.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        if(move.isRooted() || move.getDirection().isZero()) { // if no movements to move or character is frozen/disabled then return without performing any action
            return;
        }

        normalizedDirection.set(move.getDirection()).nor(); // to avoid moving faster in the diagonal directions, normalize the vector direction to unit of 1 (happens because x and y add up)
        Transform transform = Transform.MAPPER.get(entity); // set entity position using known values
        Vector2 position = transform.getPosition();
        position.set(
            position.x + move.getMaxSpeed() * normalizedDirection.x * deltaTime, // deltaTime is the time between two frames, doesn't matter how many frames any computer has, speed value and change is set accordingly
            position.y + move.getMaxSpeed() * normalizedDirection.y * deltaTime
        );

    }
}
