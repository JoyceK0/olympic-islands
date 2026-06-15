package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.component.CameraFollow;
import com.github.JoyceK0.Olympic_Islands.component.Transform;

public class CameraSystem extends IteratingSystem{
    public static final float CAM_OFFSET_Y =1f; //makes sprite in middle of screen
    //declare variable and type
    private final Camera camera;
    private Vector2 targetPosition;
    private float mapW; //total map width in world units
    private float mapH;//total map height in world units
    private float smoothingfactor;

    public CameraSystem(Camera camera){
        super(Family.all(CameraFollow.class, Transform.class).get());
        this.camera = camera;
        this.targetPosition = new Vector2();
        this.smoothingfactor = 4f;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        calcTargetPosition(transform.getPosition());

        float progress =  smoothingfactor*deltaTime; //smoothes out camera view
        float smoothedX = MathUtils.lerp(camera.position.x, this.targetPosition.x, progress);
        float smoothedY = MathUtils.lerp(camera.position.y, this.targetPosition.y, progress);
        camera.position.set(smoothedX, smoothedY, camera.position.z);

    }

    private void calcTargetPosition(Vector2 entityPosition){//calculates correct camera position
        float targetX = entityPosition.x;
        float camHalfW = camera.viewportWidth*0.5f;
        if(mapW > camHalfW){
            float min = Math.min(camHalfW, mapW - camHalfW);
            float max = Math.max(camHalfW, mapW - camHalfW);
            targetX = MathUtils.clamp(targetX, min, max);
        }

        float targetY = entityPosition.y +CAM_OFFSET_Y;// apply offset on y-axis
        float camHalfH = camera.viewportWidth*0.5f;
        if(mapH > camHalfH){
            float min = Math.min(camHalfH, mapH - camHalfH);
            float max = Math.max(camHalfH, mapH - camHalfH);
            targetY = MathUtils.clamp(targetY, min, max);
        }
        this.targetPosition.set(targetX, targetY);//sets camera position on sprite

    }

    public void setMap(TiledMap tiledMap){ ////  map dimensions from tiled properties and converts to world units
        Integer width = tiledMap.getProperties().get("width", 0, Integer.class);
        Integer tileW = tiledMap.getProperties().get("tilewidth", 0, Integer.class);
        Integer height = tiledMap.getProperties().get("height", 0, Integer.class);
        Integer tileH = tiledMap.getProperties().get("tileheight", 0, Integer.class);

        this.mapW = width*tileW* GdxGame.UNIT_SCALE;//width of the map shown to player
        this.mapH = height*tileH*GdxGame.UNIT_SCALE;//height of the map shown the player

        Entity camEntity =getEntities().first();
        if(camEntity == null){
            return;
        }
        processEntity(camEntity, 0f);

    }
}
