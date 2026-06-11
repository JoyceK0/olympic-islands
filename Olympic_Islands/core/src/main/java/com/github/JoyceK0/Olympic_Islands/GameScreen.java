package com.github.JoyceK0.Olympic_Islands;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;
import com.github.JoyceK0.Olympic_Islands.asset.MapAsset;
import com.github.JoyceK0.Olympic_Islands.system.RenderSystem;
import com.github.JoyceK0.Olympic_Islands.tiled.TiledAshleyConfigurator;
import com.github.JoyceK0.Olympic_Islands.tiled.TiledService;

import java.util.function.Consumer;

// uses an entity component system structure. An entity is an ID. Then there are components, such as a transform
// component that has position and scaling of objects. Then there may be another graphic component that has texture and
// color of objects.

/** First screen of the application. Displayed after the application is created. */
public class GameScreen extends ScreenAdapter {
    private final GdxGame game;
    private final Batch batch;
    private final AssetService assetService;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final Engine engine; // core class of the Ashley entity component system used for ECS
    private final TiledService tiledService;
    private final TiledAshleyConfigurator tiledAshleyConfigurator;

    public GameScreen(GdxGame game){
        this.game = game;
        this.assetService = game.getAssetService();
        this.viewport = game.getViewport();
        this.camera = game.getCamera();
        this.batch = game.getBatch();
        this.tiledService = new TiledService(this.assetService);
        this.engine = new Engine();
        this.tiledAshleyConfigurator = new TiledAshleyConfigurator(this.engine, this.assetService);

        this.engine.addSystem(new RenderSystem(this.batch, this.viewport, this.camera));
    }

    @Override
    public void show() {
        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        this.tiledService.setMapChangeConsumer(renderConsumer);
        this.tiledService.setLoadObjectConsumer(this.tiledAshleyConfigurator::onLoadObject);

        TiledMap tiledMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(tiledMap);

    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
    }

    @Override
    public void render(float delta) { // delta is the time between two frames
        delta = Math.min(delta, 1/30f);
        this.engine.update(delta); // updates all systems

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
            System.out.println("W was just pressed");
        }
    }

    @Override
    public void dispose() {
        for (EntitySystem system : this.engine.getSystems()){
            if(system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
    }
}
