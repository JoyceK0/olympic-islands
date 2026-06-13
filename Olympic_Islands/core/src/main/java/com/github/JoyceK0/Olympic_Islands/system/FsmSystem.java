package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.JoyceK0.Olympic_Islands.component.Fsm;

public class FsmSystem extends IteratingSystem {
    public FsmSystem() {
        super(Family.all(Fsm.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Fsm.MAPPER.get(entity).getAnimationFsm().update();
    }
}
