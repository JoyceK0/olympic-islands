package com.github.JoyceK0.Olympic_Islands.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.JoyceK0.Olympic_Islands.component.Controller;

public class GameControllerState implements ControllerState{
    // now controller logic changes for when on game screen

    private final ImmutableArray<Entity> controllerEntities; //entities with a control property

    public GameControllerState(Engine engine){
        this.controllerEntities = engine.getEntitiesFor(Family.all(Controller.class).get());
    }

    //Constructor


    @Override
    public void keyDown(Command command) {
        for(Entity entity : controllerEntities) {
            Controller.MAPPER.get(entity).getPressedCommands().add(command);
        }
    }

    @Override
    public void keyUp(Command command) {
        for(Entity entity : controllerEntities) {
            Controller.MAPPER.get(entity).getReleasedCommands().add(command);
        }
    }
}
