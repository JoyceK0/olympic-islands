package com.github.JoyceK0.Olympic_Islands.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class KeyboardController extends InputAdapter {

    private static final Map<Integer, Command> KEY_MAPPING = Map.ofEntries(
        Map.entry(Input.Keys.W, Command.UP),
        Map.entry(Input.Keys.S, Command.DOWN),
        Map.entry(Input.Keys.A, Command.LEFT),
        Map.entry(Input.Keys.D, Command.RIGHT),
        Map.entry(Input.Keys.SPACE, Command.SELECT),
        Map.entry(Input.Keys.ESCAPE, Command.CANCEL)
    ); // Maps input keys to the game commands enumeration

    private final boolean[] commandState; // keeps track of keys which were pressed before to prevent re-doing commands previously done
    private final Map<Class<? extends ControllerState>, ControllerState> stateCache;
    private ControllerState activeState;

    public KeyboardController(Class<? extends ControllerState> initialState, Engine engine) {
        this.stateCache = new HashMap<>();
        this.activeState = null;
        this.commandState = new boolean[Command.values().length];

        this.stateCache.put(IdleControllerState.class, new IdleControllerState());
        this.stateCache.put(GameControllerState.class, new GameControllerState(engine));
        setActiveState(initialState);
    }

    public void setActiveState(Class<? extends ControllerState> stateClass) { // set state of keyboard input from idle controller to game and vice versa
        ControllerState controllerState = stateCache.get(stateClass);
        if(controllerState == null) {
            throw new GdxRuntimeException("No state with class " + stateClass + " found in the state cache");
        }

        for(Command command : Command.values()) {
            if(this.activeState!=null && this.commandState[command.ordinal()]) {
                this.activeState.keyUp(command); // release command previously pressed
            }
            this.commandState[command.ordinal()] = false; // set everything to false and start fresh
        }

        this.activeState = controllerState;
    }

    // keydown/keyup call, checks for valid key and check is game command available for that specific key
    // in that specific case, if yes then run that command, if not then return false that no command was run

    @Override
    public boolean keyDown(int keycode) {
        Command command = KEY_MAPPING.get(keycode);
        if(command == null) return false;

        this.commandState[command.ordinal()] = true; // set as true that the key is pressed and its down function was run
        this.activeState.keyDown(command);
        return true;
    }


    @Override
    public boolean keyUp(int keycode) {
        Command command = KEY_MAPPING.get(keycode);
        if(command == null) return false;
        if(!this.commandState[command.ordinal()]) return false; // if the command was already false before, then do not perform the keyUp command again

        this.commandState[command.ordinal()] = false; // if not set to false, then set to false and perform keyUp command
        this.activeState.keyUp(command);
        return true;
    }
}
