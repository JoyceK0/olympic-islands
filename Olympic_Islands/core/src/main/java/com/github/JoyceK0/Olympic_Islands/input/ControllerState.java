package com.github.JoyceK0.Olympic_Islands.input;

public interface ControllerState {
    // interfaces can only have attributes and abstract methods. They do not traditionally create instances of themselves and remain the same,
    // accessible universally. Also supports multiple inheritance, since it is kept general to everything, it can be implemented by multiple
    // different classes without errors. Also doesn't require constructors

    // This interface maps the base logic for key presses depends on screen the player is on currently,
    // so essentially this is game-specific command logic which provides a blueprint of the required methods for keyboard input processing

    void keyDown(Command command); // process when a command is pressed down

    default void keyUp(Command command) { // process when a command that was previously pressed is just released
    }

}
