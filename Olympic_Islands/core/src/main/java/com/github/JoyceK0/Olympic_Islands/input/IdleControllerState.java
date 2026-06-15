package com.github.JoyceK0.Olympic_Islands.input;

import com.github.JoyceK0.Olympic_Islands.system.DialogueSystem;

public class IdleControllerState implements ControllerState {

    // similar to game controller state but for keyboard input when a player is on an idle screen with no movement like a cutscene or dialogue screen

    // Constructor, no arguments required since this is just the middle man for conversation between the input and the dialogue system
    public IdleControllerState() {
    }


    // Override the keyDown method since for the dialogue screen you only process when the key input first goes high (aka when its pressed)
    // such as when you click SELECT (spacebar) to advance the dialogue. keyUp Override is left blank since no processing required for that

    @Override
    public void keyDown(Command command) {
        // if a dialogue is currently running on screen, intercept the SELECT command only which advances the dialogue forward
        if (DialogueSystem.isDialogueActive()) {
            if (command == Command.SELECT) {
                DialogueSystem.advanceDialogue();
            }
        }
    }

    // only overridden because the implemented interface demands this method to be applied in the class
    @Override
    public void keyUp(Command command) {
    }

}
