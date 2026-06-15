package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsDebugRenderSystem extends EntitySystem implements Disposable {

    // This class is purely to enable debugging mode when rendering collision detection using box 2D
    // Essentially, this class when enabled lets the user see the collision boxes of every entity in the game, including the tiles
    // This way when testing the collision system you can see how the collision boxes interact

    private final World physicWorld;
    private final Box2DDebugRenderer box2DDebugRenderer; // keeps track of all collision objects in the physics world, their states, renders and manages them
    private final Camera camera;

    public PhysicsDebugRenderSystem(World physicWorld, Camera camera) {
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.physicWorld = physicWorld;
        this.camera = camera;
        setProcessing(false); // enables and disables debug rendering, basically stopping the execution of the update() method untile set to true
        // we disable it since we do not require it for the final product
    }

    // Pass the camera and the physic world into the debug renderer to render the collision objects
    @Override
    public void update(float deltaTime) {
        this.box2DDebugRenderer.render(physicWorld, camera.combined);
    }

    // since this is a standalone render system, the system must be disposed to save memory space when the game is closed
    @Override
    public void dispose() {
        this.box2DDebugRenderer.dispose();
    }
}
