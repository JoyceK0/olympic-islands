package com.github.JoyceK0.Olympic_Islands.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class KeyboardController extends InputAdapter {
    // input adapter is an empty implementation of the default input processor interface in LibGDX. It lets you override the interface's methods and commands
    // without being forced to write out every single interface method like you would be if implementing the interface directly

    // This class manages the controller states and relevant methods such as state cache, mapping of input keys, keyDown/Up method implementation, and
    // keeps track of the active controller state, idle or game

    // this is a Java map which maps every required keyboard input key to the corresponding command enum constant, simpler for processing
    // rather than using conditional if-elseif-else statements every time
    private static final Map<Integer, Command> KEY_MAPPING = Map.ofEntries(
        Map.entry(Input.Keys.W, Command.UP),
        Map.entry(Input.Keys.S, Command.DOWN),
        Map.entry(Input.Keys.A, Command.LEFT),
        Map.entry(Input.Keys.D, Command.RIGHT),
        Map.entry(Input.Keys.SPACE, Command.SELECT),
        Map.entry(Input.Keys.ESCAPE, Command.CANCEL)
    );

    private final boolean[] commandState; // keeps track of keys which were pressed before to prevent re-doing commands previously done

    private final Map<Class<? extends ControllerState>, ControllerState> stateCache;
    // instead of having to constantly instantiate the controller state classes, it stores their pre-existing values in a cache, speeding up
    // processing and reducing the need for java garbage collection

    private ControllerState activeState; // this keeps track of which controller state is active, the game state or the idle/dialogue state

    // Constructor, instantiates all defaults
    public KeyboardController(Class<? extends ControllerState> initialState, Engine engine) {// the Class<? extends ControllerState> helps take either IdleControllerState or GameControllerState as an argument
        this.stateCache = new HashMap<>();
        this.activeState = null;
        this.commandState = new boolean[Command.values().length];

        // putting both controller states in the state cache when instantiating to save memory
        this.stateCache.put(IdleControllerState.class, new IdleControllerState());
        this.stateCache.put(GameControllerState.class, new GameControllerState(engine));
        setActiveState(initialState);
    }


    // this sets the active state for keyboard input processing, which can be either idle or game in our program
    public void setActiveState(Class<? extends ControllerState> stateClass) { // set state of keyboard input from idle controller to game and vice versa
        ControllerState controllerState = stateCache.get(stateClass); // get the cache data for the specific state
        if(controllerState == null) { // if the controller state is null then the state passed is not a valid state saved in the cache during instantiation
            throw new GdxRuntimeException("No state with class " + stateClass + " found in the state cache");
        }

        // if it is a valid state, then reset the cache for the old state
        for(Command command : Command.values()) {
            if(this.activeState!=null && this.commandState[command.ordinal()]) { // if any commands exist in the old command memory for the state, they must be erased
                // for example, if a player was holding down the up key when the dialogue box was activated, then when game state would be loaded back the game
                // would think the character is still moving upwards, which can be an issue.
                // That is why force the keyUp command to reset the key input (below).
                this.activeState.keyUp(command);
            }
            this.commandState[command.ordinal()] = false; // set the command place to false/default and start fresh
        }

        this.activeState = controllerState; // set the current activeState of keyboard input as the refreshed controller state
    }

    // keydown/keyup call, checks for valid key and check is game command available for that specific key
    // in that specific controller state. If yes then run that command, if not then return false that no command was run

    @Override
    public boolean keyDown(int keycode) {
        Command command = KEY_MAPPING.get(keycode); // get the corresponding command value for the key just pressed down
        if(command == null) return false; // if none was returned then no valid key pressed

        this.commandState[command.ordinal()] = true; // set as true that the key is pressed and its down function was/is to be run
        this.activeState.keyDown(command); // activate the keyDown function for that specific state
        return true; // keyDown successful
    }

    // a similar setup is used for keyUp as keyDown
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
