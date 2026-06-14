package com.github.JoyceK0.Olympic_Islands.input;

import com.github.JoyceK0.Olympic_Islands.system.DialogueSystem;

public class IdleControllerState implements ControllerState {
    // controller commands when a player is on an idle screen like a cutscene or dialogue

    @Override
    public void keyDown(Command command) {
        // if a dialogue is currently running on screen, intercept the SELECT command
        if (DialogueSystem.isDialogueActive()) {
            if (command == Command.SELECT) {
                DialogueSystem.advanceDialogue();
            }
        }
    }
}
