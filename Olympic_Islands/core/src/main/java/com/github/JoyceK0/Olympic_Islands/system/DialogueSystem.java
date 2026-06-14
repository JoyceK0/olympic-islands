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
import com.github.JoyceK0.Olympic_Islands.DialogueHash;
import com.github.JoyceK0.Olympic_Islands.DialogueList;
import com.github.JoyceK0.Olympic_Islands.EventMap;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.input.GameControllerState;
import com.github.JoyceK0.Olympic_Islands.input.IdleControllerState;
import com.github.JoyceK0.Olympic_Islands.input.KeyboardController;

import java.util.ArrayList;

public class DialogueSystem extends EntitySystem {

    // SINGLETON INSTANCE: Safely bridges static context back to instance mechanics
    private static DialogueSystem instance = null;

    private final GdxGame game;
    private final Batch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    private final DialogueHash dialogueHash;
    private final EventMap eventMap;
    private final KeyboardController keyboardController;

    private static boolean isActive = false;
    private static DialogueList currentDialogue = null;
    private static int currentTextIndex = 0;

    private boolean isControllerFrozen = false;
    private static boolean justClosed = false;

    // Portrait Caching Variables to prevent mid-frame garbage collection stuttering
    private Texture currentPortraitTexture = null;
    private String lastLoadedPath = "";

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

    public static boolean isDialogueActive() {
        return isActive;
    }

    public void startDialogue(String npcName) {
        if (justClosed) return;

        DialogueList node = dialogueHash.getNpc(npcName);
        if (node == null) return;

        if (node.triggerEvent != null && !node.triggerEvent.isEmpty() && !node.triggerEvent.equals("null")) {
            var event = eventMap.getEvent(node.triggerEvent);
            if (event == null || !event.completion) {
                System.out.println("This conversation is locked.");
                return;
            }
        }

        currentDialogue = node;
        currentTextIndex = 0;
        currentDialogue.chosenOption = -1;
        isActive = true;

        keyboardController.setActiveState(IdleControllerState.class);
        isControllerFrozen = true;
    }

    public static void advanceDialogue() {
        if (!isActive || currentDialogue == null || instance == null) return;

        // Halt advancement if choice options are on screen and no choice has been made
        if (currentDialogue.path2name != null && currentDialogue.chosenOption == -1) {
            return;
        }

        // Step through the individual text lines of the current node
        if (currentTextIndex < currentDialogue.dialogue.size() - 1) {
            currentTextIndex++;
        } else {
            // Handle marking any associated game events as completed
            if (currentDialogue.causesEvent != null && !currentDialogue.causesEvent.isEmpty() && !currentDialogue.causesEvent.equals("null")) {
                var event = instance.eventMap.getEvent(currentDialogue.causesEvent);
                if (event != null) {
                    event.completed();
                    System.out.println("Event completed: " + currentDialogue.causesEvent);
                }
            }

            // Peek at the upcoming node in the sequence
            DialogueList nextNode = currentDialogue.next();

            // RUN LOOKAHEAD EVALUATION RULES:
            if (nextNode == null || nextNode == currentDialogue) {
                // Rule A: There is no available dialogue after this. Keep done = false so it circulates.
                currentDialogue.done = false;

                isActive = false;
                currentDialogue = null;
                justClosed = true;
            } else {
                // Check if the next node has a trigger requirements contract
                if (nextNode.triggerEvent != null && !nextNode.triggerEvent.isEmpty() && !nextNode.triggerEvent.equals("null")) {
                    var pendingEvent = instance.eventMap.getEvent(nextNode.triggerEvent);

                    if (pendingEvent == null || !pendingEvent.completion) {
                        // Rule B: The next dialogue exists, but its trigger is NOT fulfilled yet!
                        // Do not mark the current dialogue as done, close the window, and freeze progression.
                        currentDialogue.done = false;

                        isActive = false;
                        currentDialogue = null;
                        justClosed = true;
                        return; // Exit out immediately
                    }
                }

                // Rule C: There is a next dialogue AND it either has no trigger or a fulfilled one.
                // The current node is officially exhausted. Mark it done!
                currentDialogue.done = true;

                // Transition smoothly to the next node block
                currentDialogue = nextNode;
                currentTextIndex = 0;
                currentDialogue.chosenOption = -1;
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!isActive && isControllerFrozen) {
            keyboardController.setActiveState(GameControllerState.class);
            isControllerFrozen = false;
            return;
        }

        if (!isActive && justClosed) {
            justClosed = false;
        }

        if (isActive && currentDialogue != null && currentDialogue.path2name != null && currentDialogue.chosenOption == -1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { currentDialogue.chosenOption = 1; advanceDialogue(); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { currentDialogue.chosenOption = 2; advanceDialogue(); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { currentDialogue.chosenOption = 3; advanceDialogue(); }
        }

        if (!isActive && Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            startDialogue("Zeus");
        }

        if (isActive) {
            renderDialogueBox();
        }
    }

    private void renderDialogueBox() {
        if (currentDialogue == null) return;

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float boxH = screenH * 0.25f;

        // 1. Snapshot the native world camera layout matrix
        Matrix4 oldProjection = batch.getProjectionMatrix().cpy();

        // 2. Generate a flat 2D projection space overlay
        Matrix4 screenProjection = new Matrix4();
        screenProjection.setToOrtho2D(0, 0, screenW, screenH);

        // 3. Render Background UI Layers
        shapeRenderer.setProjectionMatrix(screenProjection);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Base main dialogue strip container
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.85f));
        shapeRenderer.rect(0, 0, screenW, boxH);

        // Frame bounding boxes
        float portraitMargin = 15f;
        float portraitSize = boxH - (portraitMargin * 2f);
        float portraitX = portraitMargin;
        float portraitY = portraitMargin;

        // Draw Gold outer frame border
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(portraitX - 2f, portraitY - 2f, portraitSize + 4f, portraitSize + 4f);

        // Inner backing window slot
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.95f));
        shapeRenderer.rect(portraitX, portraitY, portraitSize, portraitSize);

        shapeRenderer.end();

        // 4. Dynamic Stream Resource Cache Loader
        String nodePath = currentDialogue.spritePath;

        if (nodePath != null && !nodePath.equals("null") && !nodePath.isEmpty()) {
            if (!nodePath.equals(lastLoadedPath)) {
                if (currentPortraitTexture != null) {
                    currentPortraitTexture.dispose();
                }
                try {
                    currentPortraitTexture = new Texture(Gdx.files.internal(nodePath));
                    lastLoadedPath = nodePath;
                } catch (Exception e) {
                    System.out.println("Failed to stream image path asset: " + nodePath);
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

        // 5. Render active assets out to screen layout space
        batch.setProjectionMatrix(screenProjection);
        batch.begin();
        batch.setColor(Color.WHITE); // Wipe out dirty tint states

        if (currentPortraitTexture != null) {
            batch.draw(currentPortraitTexture, portraitX, portraitY, portraitSize, portraitSize);
        }

        // Align typography layout bounds relative to portrait box width edge
        float textOffsetX = portraitX + portraitSize + 30f;

        // Draw Speaker Name Text
        font.setColor(Color.GOLD);
        font.draw(batch, currentDialogue.name.toUpperCase(), textOffsetX, boxH - 20f);

        // Draw Conversation Text body
        font.setColor(Color.WHITE);
        String currentText = currentDialogue.dialogue.get(currentTextIndex);
        font.draw(batch, currentText, textOffsetX, boxH - 55f);

        // Render Action Choices or Continual Help hints
        if (currentDialogue.path2name != null && currentDialogue.chosenOption == -1) {
            font.setColor(Color.CYAN);
            if (currentDialogue.path1name != null) font.draw(batch, "[1]: " + currentDialogue.path1name, textOffsetX, boxH - 95f);
            if (currentDialogue.path2name != null) font.draw(batch, "[2]: " + currentDialogue.path2name, textOffsetX, boxH - 115f);
            if (currentDialogue.path3name != null) font.draw(batch, "[3]: " + currentDialogue.path3name, textOffsetX, boxH - 135f);
        } else {
            font.setColor(Color.GRAY);
            font.draw(batch, "Press [SPACE] to continue...", screenW - 220f, 25f);
        }

        batch.end();

        // 6. Return original tracking state back to your game camera loop
        batch.setProjectionMatrix(oldProjection);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        // Prevent continuous VRAM allocation leaks if the screen environment unloads
        if (currentPortraitTexture != null) {
            currentPortraitTexture.dispose();
        }
        shapeRenderer.dispose();
        font.dispose();
    }
}
