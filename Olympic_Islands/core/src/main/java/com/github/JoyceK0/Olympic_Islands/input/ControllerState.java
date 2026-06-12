package com.github.JoyceK0.Olympic_Islands.input;

public interface ControllerState {
    // interfaces can only have attributes and abstract methods. They do not traditionally create instances of themselves and remain the same, accessible universally
    // also supports multiple inheritance, since it is kept general to everything, it can be implemented by multiple different classes without errors
    // also doesn't need constructors

    // logic for key presses depends on screen the player is on currently, so this is game-specific command logic, provides a blueprint of the required methods

    void keyDown(Command command);

    default void keyUp(Command command) {
    }

}
