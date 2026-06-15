package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.github.JoyceK0.Olympic_Islands.dialogue.DialogueHash;
import com.github.JoyceK0.Olympic_Islands.dialogue.DialogueList;
import com.github.JoyceK0.Olympic_Islands.dialogue.EventMap;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.input.GameControllerState;
import com.github.JoyceK0.Olympic_Islands.input.IdleControllerState;
import com.github.JoyceK0.Olympic_Islands.input.KeyboardController;

public class DialogueSystem extends EntitySystem {

    // This class handles the dialogue logic and displays it on the screen
    // REMINDER: This is a basic outline for basic dialogue management. it does not utilize all variables
    // and/or logic and doesn't represent the full capacity of the dialogue management system. it basically
    // just tests the dialogue code and displays for a sample viewing of how the interaction will look
    // like. It uses basic rendering and no ECS system rendering for the Dialogue system.

    private static DialogueSystem instance = null;

    // Access various game systems and classes for basic text rendering
    private final GdxGame game;
    private final Batch batch;
    private final ShapeRenderer shapeRenderer; // renders the dialogue box at the bottom of the screen
    private final BitmapFont font;

    // These are the required classes for dialogue management and control
    private final DialogueHash dialogueHash;
    private final EventMap eventMap;
    private final KeyboardController keyboardController;

    private static boolean isActive = false; // tracks if dialogue system is active or not
    private static DialogueList currentDialogue = null; // instantiates the holder variable for the current dialogue
    private static int currentTextIndex = 0; // if the npc dialogue is too long, it is broken up into chunks to prevent overflow, this tracks those chunks

    private boolean isControllerFrozen = false; // this tracks if the game controller is on or off
    private static boolean justClosed = false; // if you accidentally click spacebar twice it won't trigger the conversation again due to the justClosed tracking variable

    // variables for storing and loading information regarding the sprite picture that shows up beside the dialogue box
    private Texture currentPortraitTexture = null;
    private String lastLoadedPath = "";

    // Constructor, accesses the required variables such as DialogueHash and EventMap from the GdxGame where they were instantiated and loaded
    public DialogueSystem(GdxGame game, DialogueHash dialogueHash, EventMap eventMap, KeyboardController keyboardController) {
        this.game = game;
        this.batch = game.getBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.dialogueHash = dialogueHash;
        this.eventMap = eventMap;
        this.keyboardController = keyboardController;

        instance = this;
    }

    // Accessor
    public static boolean isDialogueActive() {
        return isActive;
    }

    // Start the dialogue
    public void startDialogue(String npcName) {
        if (justClosed) return; // do not want starting due to accidental double click of spacebar when just closed

        DialogueList dialogueList = dialogueHash.getNpc(npcName); // get the dialogue dialogueList instance
        if (dialogueList == null) return;

        // See if there is a trigger event related with the dialogue, and if yes then check is trigger event is completed and handle accordingly
        if (dialogueList.triggerEvent != null && !dialogueList.triggerEvent.isEmpty() && !dialogueList.triggerEvent.equals("null")) {
            var event = eventMap.getEvent(dialogueList.triggerEvent);
            if (event == null || !event.completion) { // if trigger event existsd and not fulfilled then exit
                System.out.println("This conversation is locked.");
                return;
            }
        }

        currentDialogue = dialogueList; // set current dialogue
        currentTextIndex = 0;
        currentDialogue.chosenOption = -1; // the chosenOption default is -1 before user picks
        isActive = true; // Dialogue system is now active, disabling character movement

        keyboardController.setActiveState(IdleControllerState.class); // set keyboard state to idle state
        isControllerFrozen = true; // disable character movement
    }

    // Everytime the spacebar is pressed advance the dialogue through basic logic
    public static void advanceDialogue() {
        if (!isActive || currentDialogue == null || instance == null) return; // if dialogue is supposed to be inactive, return without doing antyhing

        // stop advancement if choice options are on screen and no choice has been made. Options will be made when the player types the number
        if (currentDialogue.path2name != null && currentDialogue.chosenOption == -1) {
            return;
        }

        // go through the individual text lines of the current dialogueList if there are any
        if (currentTextIndex < currentDialogue.dialogue.size() - 1) {
            currentTextIndex++;
        } else {
            // mark any associated game events as completed is relevant and required
            if (currentDialogue.causesEvent != null && !currentDialogue.causesEvent.isEmpty() && !currentDialogue.causesEvent.equals("null")) {
                var event = instance.eventMap.getEvent(currentDialogue.causesEvent);
                if (event != null) { // if dialogue causes event, then mark that event as completed
                    event.completed();
                    System.out.println("Event completed: " + currentDialogue.causesEvent);
                }
            }

            // reference the next dialogue in the tree
            DialogueList nextdialogueList = currentDialogue.next();

            // Check if the next dialogue can be run based on triggerEvents and its value
            if (nextdialogueList == null || nextdialogueList == currentDialogue) {
                // if in here then there is no available dialogue after the one just completed. Keep done = false so only the last dialogue circulates.
                currentDialogue.done = false;

                isActive = false; // close dialogue system
                currentDialogue = null; // reset current dialogue
                justClosed = true; // set to true
            }

            else {
                // check if the next dialogue has a triggerEvents variable and if its completed or not
                if (nextdialogueList.triggerEvent != null && !nextdialogueList.triggerEvent.isEmpty() && !nextdialogueList.triggerEvent.equals("null")) {
                    var pendingEvent = instance.eventMap.getEvent(nextdialogueList.triggerEvent);

                    if (pendingEvent == null || !pendingEvent.completion) {
                        // if in here then the next dialogue exists, but its trigger is not complete yet
                        // do not mark the current dialogue as done, close the window, and freeze dialogue progression till here.
                        currentDialogue.done = false;

                        isActive = false;
                        currentDialogue = null;
                        justClosed = true;
                        return; // Exit out immediately
                    }
                }

                // if nothing else triggers then there is a next dialogue AND it either has no trigger or a completed one.
                currentDialogue.done = true; // mark the current dialogue as done

                // move to the next dialogue and temporary variables
                currentDialogue = nextdialogueList;
                currentTextIndex = 0;
                currentDialogue.chosenOption = -1;
            }
        }
    }


    // update the dialogue box
    @Override
    public void update(float deltaTime) {
        if (!isActive && isControllerFrozen) { // if the dialogue system is off but game controller is still frozen, then reset and permit character movement
            keyboardController.setActiveState(GameControllerState.class);
            isControllerFrozen = false;
            return;
        }

        if (!isActive && justClosed) { // reset justClose one iteration after the game was closed
            justClosed = false;
        }

        // if the dialogue is still ongoing and there is an option available but the user hasn't chosen one yet, listen for basic key inputs
        if (isActive && currentDialogue != null && currentDialogue.path2name != null && currentDialogue.chosenOption == -1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { currentDialogue.chosenOption = 1; advanceDialogue(); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { currentDialogue.chosenOption = 2; advanceDialogue(); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { currentDialogue.chosenOption = 3; advanceDialogue(); }
        }

        // if the dialogue system is inactive but the user just pressed the space key while the system is listening for the trigger, then start dialogue up again
        if (!isActive && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startDialogue("Stolk");
        }

        if (isActive) { // finally, when updating is dialogue is still ongoing, re-render the dialogue box visual with the latest data
            renderDialogueBox();
        }
    }

    // this method renders a basic dialogue box with basic text and view
    private void renderDialogueBox() {
        if (currentDialogue == null) return;

        // we use basic Gdx graphics class to render the dialogue box, but for more complex system in the future Scene2D will be preferred
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float boxH = screenH * 0.25f;

        //get the projection matrix for the camera
        Matrix4 oldProjection = batch.getProjectionMatrix().cpy();

        // generate the area where we will render the dialogue box
        Matrix4 screenProjection = new Matrix4();
        screenProjection.setToOrtho2D(0, 0, screenW, screenH);

        // start basic background rendering systems and set the basic background shape
        shapeRenderer.setProjectionMatrix(screenProjection);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // set some basic properties for the background shape
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.85f)); // keeping it a basic black for now with 85% opacity
        shapeRenderer.rect(0, 0, screenW, boxH);

        // basic sizing for the image of the character speaking
        float portraitMargin = 15f;
        float portraitSize = boxH - (portraitMargin * 2f);
        float portraitX = portraitMargin;
        float portraitY = portraitMargin;

        // draw Gold outer frame border of the sprite image
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(portraitX - 2f, portraitY - 2f, portraitSize + 4f, portraitSize + 4f);

        // a smaller, lighter shape layer on the background for graphics
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.95f));
        shapeRenderer.rect(portraitX, portraitY, portraitSize, portraitSize);

        shapeRenderer.end(); // close the shape renderer, basic shapes are completed

        //find the sprite path for the image
        String dialogueListPath = currentDialogue.spritePath;

        // ensure the image path is usable, then render
        if (dialogueListPath != null && !dialogueListPath.equals("null") && !dialogueListPath.isEmpty()) {
            if (!dialogueListPath.equals(lastLoadedPath)) {
                if (currentPortraitTexture != null) {
                    currentPortraitTexture.dispose();
                }
                try {
                    currentPortraitTexture = new Texture(Gdx.files.internal(dialogueListPath));
                    lastLoadedPath = dialogueListPath;
                } catch (Exception e) {
                    System.out.println("Failed to stream image path asset: " + dialogueListPath);
                    currentPortraitTexture = null;
                    lastLoadedPath = "";
                }
            }
        } else {
            if (currentPortraitTexture != null) {
                currentPortraitTexture.dispose();
                currentPortraitTexture = null;
            }
            lastLoadedPath = "";
        }

        //finalize some visual properties and background systems
        batch.setProjectionMatrix(screenProjection);
        batch.begin();
        batch.setColor(Color.WHITE); // this prevents unnecessary tinting on graphics

        if (currentPortraitTexture != null) {
            batch.draw(currentPortraitTexture, portraitX, portraitY, portraitSize, portraitSize);
        }

        // align the boundary for the text boxes
        float textOffsetX = portraitX + portraitSize + 30f;

        // draw speaker name
        font.setColor(Color.GOLD);
        font.draw(batch, currentDialogue.name.toUpperCase(), textOffsetX, boxH - 20f);

        // draw the main dialogue text on the screen
        font.setColor(Color.WHITE);
        String currentText = currentDialogue.dialogue.get(currentTextIndex);
        font.draw(batch, currentText, textOffsetX, boxH - 55f);

        // render option choices and helper hints to help the user navigate dialogues
        if (currentDialogue.path2name != null && currentDialogue.chosenOption == -1) {
            font.setColor(Color.CYAN);
            if (currentDialogue.path1name != null) font.draw(batch, "[1]: " + currentDialogue.path1name, textOffsetX, boxH - 95f);
            if (currentDialogue.path2name != null) font.draw(batch, "[2]: " + currentDialogue.path2name, textOffsetX, boxH - 115f);
            if (currentDialogue.path3name != null) font.draw(batch, "[3]: " + currentDialogue.path3name, textOffsetX, boxH - 135f);
        } else {
            font.setColor(Color.GRAY);
            font.draw(batch, "Press [SPACE] to continue...", screenW - 220f, 25f);
        }

        batch.end(); // close the asset batch

        // set the screen to the original setting for game screen
        batch.setProjectionMatrix(oldProjection);
    }

    // dispose of assets and free up memory allocation, specifically the sprite image frame, the font, and the shape renderer
    @Override
    public void removedFromEngine(Engine engine) {
        if (currentPortraitTexture != null) {
            currentPortraitTexture.dispose();
        }
        shapeRenderer.dispose();
        font.dispose();
    }
}
