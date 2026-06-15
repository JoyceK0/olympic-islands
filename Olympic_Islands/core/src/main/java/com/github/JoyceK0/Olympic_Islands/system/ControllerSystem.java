package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.JoyceK0.Olympic_Islands.asset.SoundAsset;
import com.github.JoyceK0.Olympic_Islands.audio.AudioService;
import com.github.JoyceK0.Olympic_Islands.component.Controller;
import com.github.JoyceK0.Olympic_Islands.component.Move;
import com.github.JoyceK0.Olympic_Islands.input.Command;

public class ControllerSystem extends IteratingSystem {

    // This class manages all keyboard inputs and processes from the keyboard controller class
    // This extends the iterating system, which essentially helps narrow down the affected entities
    // for the system rather than looping through the entire entity mapper to grab the required entities

    // When keyboard inputs are toggled, audio and dialogue is also toggled for some cases, so gain access to those programs
    private final AudioService audioService;
    private final DialogueSystem dialogueSystem;

    //Constructor, requires the audioService and dialogueSystem of the GdxGame
    public ControllerSystem(AudioService audioService, DialogueSystem dialogueSystem) {
        super(Family.all(Controller.class).get()); // get all entities with controller component attached (meaning player should be able to control that)
        // the Family.all function is provided by the IteratingSystem, cutting down a lot of repetitive looping search code
        this.audioService = audioService;
        this.dialogueSystem = dialogueSystem;
    }

    // Customize how to handle the entities of the controller class in this system
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Controller controller = Controller.MAPPER.get(entity); // get the controller class and its attributes for the specific entity
        if(controller.getPressedCommands().isEmpty() && controller.getReleasedCommands().isEmpty()) { // check to see if any commands are available to execute
            return; // if no commands are live then do nothing
        }

        // This automatically works for dual keys pressed together like UP and RIGHT due to its structure

        for (Command command : controller.getPressedCommands()) { // for every command in the pressedCommands ArrayList perform the required method

            switch (command) {

                // +x is right, -x is left, and +y is up, -y is down
                // so when up/down/left/right are pressed, then the specific value is set to increment continuously by a certain value

                case UP -> moveEntity(entity, 0f, 1f);
                case DOWN -> moveEntity(entity, 0f, -1f);
                case LEFT -> moveEntity(entity, -1f, 0f);
                case RIGHT -> moveEntity(entity, 1f, 0f);
                case SELECT -> entityToggle(entity); // handle the dialogue toggling or object interaction

            }

        }
        controller.getPressedCommands().clear(); // commands have been executed, clear old data to prevent the move direction to keep incrementing while the key is still pressed, which would cause the entity to accelerate


        // Similar structure for released commands as pressed commands
        for (Command command : controller.getReleasedCommands()) {

            switch (command) {

                // +x is right, -x is left, and +y is up, -y is down
                // so when up/down/left/right are released, then the opposite value is inputted so the increase value zeroes out and there is no more incrementation

                case UP -> moveEntity(entity, 0f, -1f);
                case DOWN -> moveEntity(entity, 0f, 1f);
                case LEFT -> moveEntity(entity, 1f, 0f);
                case RIGHT -> moveEntity(entity, -1f, 0f);

            }

        }
        controller.getReleasedCommands().clear(); // commands have been executed, clear old data
    }

    // the function to execute when the spacebar is pressed (SELECT)
    private void entityToggle(Entity entity) {
        audioService.playSound(SoundAsset.CLICK);

        // For testing purposes right now, use a test "Stolk" NPC structure:
        String targetNpcName = "Stolk";
        // traditionally for dialogue handling, we would simply use box2D's function and components to determine
        // the closest entity to the player labeled "NPC", and then start the conversation by passing that NPC's name

        if (targetNpcName != null) {
            dialogueSystem.startDialogue(targetNpcName); // starts the dialogue in the dialogue system, which handles in-dialogue input on its own
        }

    }

    // the move entity function for changing player's position
    private void moveEntity(Entity entity, float directionX, float directionY) {

        Move move = Move.MAPPER.get(entity); // use the Move mapper to get the Move class instance for the current entity
        if(move == null) return;

        move.getDirection().x += directionX; // add direction value
        move.getDirection().y += directionY; // add direction value

    }
}
