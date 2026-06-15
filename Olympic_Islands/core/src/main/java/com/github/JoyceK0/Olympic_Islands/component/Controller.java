package com.github.JoyceK0.Olympic_Islands.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.github.JoyceK0.Olympic_Islands.input.Command;

import java.util.ArrayList;
import java.util.List;

public class Controller implements Component {

    // this class declares the controller class which contains key attributes for user input including the list of pressed keys and just released keys
    // It also creates a new component mapper to manage this property for all entities

    public static final ComponentMapper<Controller> MAPPER = ComponentMapper.getFor(Controller.class);
    // create the component mapper (definition of component mapper found in Physic component)

    // attributes of the controller component. Their types are of the command enumeration, which implies that they can only hold the properties and/or
    // values specified by the command enum and nothing else.
    private final List<Command> pressedCommands; // this helps track which of the fixed commands are currently being pressed down by the user
    private final List<Command> releasedCommands; // this helps track which of the fixed commands were just released by the player after being held down

    // Constructor, instantiate default empty lists. The list class is implemented by ArrayList, therefore ArrayList can be instantiated in List type
    public Controller() {
        this.pressedCommands = new ArrayList<>();
        this.releasedCommands = new ArrayList<>();
    }


    // Accessors
    public List<Command> getPressedCommands() {
        return pressedCommands;
    }

    public List<Command> getReleasedCommands() {
        return releasedCommands;
    }
}
