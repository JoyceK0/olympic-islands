package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.github.JoyceK0.Olympic_Islands.component.CameraFollow;
import com.github.JoyceK0.Olympic_Islands.component.Transform;

public class CameraSystem extends InteratingSystem{

    private final Camera camera;

    public CameraSystem(Camera camera){
        super(family.all(CameraFollow.class, Transform.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);

            camera.position.set(transform.getPosition().x, transform.getPosition().y, camera.position.z);

    }
}
