package com.github.JoyceK0.Olympic_Islands.input;

public class IdleControllerState implements ControllerState{
    // controller commands when a player is on an idle screen like a cutscene

    @Override
    public void keyDown(Command command) {
        // nothing happens, we don't want any user input when idle
    }
}
