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

    private final AudioService audioService;

    public ControllerSystem(AudioService audioService) {
        super(Family.all(Controller.class).get()); // get all entities with controller component attached (meaning player should be able to control that)
        this.audioService = audioService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Controller controller = Controller.MAPPER.get(entity);
        if(controller.getPressedCommands().isEmpty() && controller.getReleasedCommands().isEmpty()) {
            return; // if no commands are live then do nothing
        }

        // This automatically works for dual keys pressed together like UP and RIGHT due to its structure

        for (Command command : controller.getPressedCommands()) {

            switch (command) {

                // +x is right, -x is left, and +y is up, -y is down
                // so when up/down/left/right are pressed, then the specific value is set to increment continuously by a certain value

                case UP -> moveEntity(entity, 0f, 1f);
                case DOWN -> moveEntity(entity, 0f, -1f);
                case LEFT -> moveEntity(entity, -1f, 0f);
                case RIGHT -> moveEntity(entity, 1f, 0f);
                case SELECT -> entityToggle(entity);

            }

        }
        controller.getPressedCommands().clear(); // commands have been executed, clear old data



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

    private void entityToggle(Entity entity) {
        audioService.playSound(SoundAsset.CLICK);
    }

    private void moveEntity(Entity entity, float directionX, float directionY) {

        Move move = Move.MAPPER.get(entity);
        if(move == null) return;

        move.getDirection().x += directionX;
        move.getDirection().y += directionY;

    }
}
