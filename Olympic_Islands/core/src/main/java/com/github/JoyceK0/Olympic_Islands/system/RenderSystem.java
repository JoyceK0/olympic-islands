package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem; // makes it so if an entity is behind the player, the player
// is rendered first. if the entity is in front of the player, the entity is rendered first.
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.component.Graphic;
import com.github.JoyceK0.Olympic_Islands.component.Transform;

import java.util.Comparator;

public class RenderSystem extends SortedIteratingSystem implements Disposable {
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Batch batch;
    private final Viewport viewport;
    private final OrthographicCamera camera;

    public RenderSystem(Batch batch, Viewport viewport, OrthographicCamera camera) {
        super(
            Family.all(Transform.class, Graphic.class).get(),
            Comparator.comparing(Transform.MAPPER::get)
        );
        this.batch = batch;
        this.viewport = viewport;
        this.camera = camera;
        this.mapRenderer = new OrthogonalTiledMapRenderer(null, GdxGame.UNIT_SCALE, this.batch);
    }

    @Override
    public void update(float deltaTime) {
        this.viewport.apply();
        this.batch.setColor(Color.WHITE);
        this.mapRenderer.setView(this.camera);
        this.mapRenderer.render();

        forceSort(); // sorts the order in which objects are rendered based on their position
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        Graphic graphic = Graphic.MAPPER.get(entity);
        if(graphic.getRegion() == null){
            return;
        }

        Vector2 position = transform.getPosition();
        Vector2 scaling = transform.getScaling();
        Vector2 size = transform.getSize();
        this.batch.setColor(graphic.getColor());
        this.batch.draw(
            graphic.getRegion(),
            position.x - (1f -scaling.x) * size.x * 0.5f, // 36:50 in #04 ECS video for explanation
            position.y - (1f -scaling.y) * size.y * 0.5f,
            size.x * 0.5f, size.y*0.5f,
            size.x, size.y,
            scaling.x, scaling.y,
            transform.getRotationDeg()
        );
    }

    public void setMap(TiledMap tiledMap) {  this.mapRenderer.setMap(tiledMap);   }

    @Override
    public void dispose() {  this.mapRenderer.dispose();  }
}
