package com.github.JoyceK0.Olympic_Islands.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.JoyceK0.Olympic_Islands.component.Controller;

public class GameControllerState implements ControllerState{ // implements the interface ControllerState, which requires this to have keyUp and keyDown methods

    // this class specifically handles the state for when the user want to input through keyboard when on the game screen
    // It helps the game universally identify the controller state and also performs the main processing for the Controller System's attributes
    // pressedCommands and releasedCommands.

    private final ImmutableArray<Entity> controllerEntities; // this is a key variable which helps track which entities are impacted by the controller

    // Constructor
    public GameControllerState(Engine engine){
        //creates an immutable array of all entity references with a control property assigned to them in the game engine
        this.controllerEntities = engine.getEntitiesFor(Family.all(Controller.class).get());
    }

    // Overridden methods through interface to apply custom logic for when in the game controller state

    // if in the game controller state, then when a keyboard command is pressed, add that command to the pressed commands arrayList for all the controller entities
    @Override
    public void keyDown(Command command) {
        for(Entity entity : controllerEntities) {
            Controller.MAPPER.get(entity).getPressedCommands().add(command);
        }
    }

    // if in the game controller state, then when a keyboard command is released, add that command to the released commands arrayList for all the controller entities
    @Override
    public void keyUp(Command command) {
        for(Entity entity : controllerEntities) {
            Controller.MAPPER.get(entity).getReleasedCommands().add(command);
        }
    }
}
