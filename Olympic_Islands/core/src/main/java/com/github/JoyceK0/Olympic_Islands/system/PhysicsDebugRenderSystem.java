package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsDebugRenderSystem extends EntitySystem implements Disposable {

    private final World physicWorld;
    private final Box2DDebugRenderer box2DDebugRenderer; // keeps track of all objects in the physics world, their states, renders and manages them
    private final Camera camera;

    public PhysicsDebugRenderSystem(World physicWorld, Camera camera) {
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.physicWorld = physicWorld;
        this.camera = camera;
        // setProcessing(false); // enables and disables debug rendering
    }


    @Override
    public void update(float deltaTime) {
        this.box2DDebugRenderer.render(physicWorld, camera.combined);
    }

    @Override
    public void dispose() {
        this.box2DDebugRenderer.dispose();
    }
}
