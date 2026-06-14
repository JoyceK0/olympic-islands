package com.github.JoyceK0.Olympic_Islands.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.asset.MapAsset;
import com.github.JoyceK0.Olympic_Islands.component.Move;
import com.github.JoyceK0.Olympic_Islands.input.GameControllerState;
import com.github.JoyceK0.Olympic_Islands.input.KeyboardController;
import com.github.JoyceK0.Olympic_Islands.system.*;
import com.github.JoyceK0.Olympic_Islands.tiled.TiledAshleyConfigurator;
import com.github.JoyceK0.Olympic_Islands.tiled.TiledService;

import java.util.function.Consumer;

// uses an entity component system structure. An entity is an ID. Then there are components, such as a transform
// component that has position and scaling of objects. Then there may be another graphic component that has texture and
// color of objects.

/** First screen of the application. Displayed after the application is created. */
public class GameScreen extends ScreenAdapter {
    private final Engine engine; // core class of the Ashley entity component system used for ECS
    private final TiledService tiledService;
    private final TiledAshleyConfigurator tiledAshleyConfigurator;
    private final KeyboardController keyboardController;
    private final GdxGame game;
    private final World physicWorld;


    public GameScreen(GdxGame game){
        this.game = game;
        this.physicWorld = new World(Vector2.Zero, true);
        this.physicWorld.setAutoClearForces(false);
        this.tiledService = new TiledService(game.getAssetService(), this.physicWorld);
        this.engine = new Engine();
        this.tiledAshleyConfigurator = new TiledAshleyConfigurator(this.engine, game.getAssetService(), physicWorld);
        this.keyboardController = new KeyboardController(GameControllerState.class, engine);

        this.engine.addSystem(new ControllerSystem());
        this.engine.addSystem(new PhysicMoveSystem());
        this.engine.addSystem(new CameraSystem(game.getCamera())); //before render
        this.engine.addSystem(new FsmSystem());
        this.engine.addSystem(new FacingSystem());
        this.engine.addSystem(new PhysicsSystem(physicWorld, 1/60f)); //lower the interval/ refresh rate for physics the more accurate the physics implementation in
        this.engine.addSystem(new AnimationSystem((game.getAssetService())));
        this.engine.addSystem(new RenderSystem(game.getBatch(), game.getViewport(), game.getCamera()));
        this.engine.addSystem(new PhysicsDebugRenderSystem(physicWorld, game.getCamera()));

    }

    @Override
    public void show() {

        game.setInputProcessors(keyboardController);
        keyboardController.setActiveState(GameControllerState.class);

        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        Consumer<TiledMap> cameraConsumer = this.engine.getSystem(CameraSystem.class)::setMap;
        this.tiledService.setMapChangeConsumer(renderConsumer.andThen(cameraConsumer));
        this.tiledService.setLoadObjectConsumer(this.tiledAshleyConfigurator::onLoadObject);
        this.tiledService.setLoadTileConsumer(tiledAshleyConfigurator::onLoadTile);

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
    }

    @Override
    public void dispose() {
        for (EntitySystem system : this.engine.getSystems()){
            if(system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
        this.physicWorld.dispose(); // also needs to clear files as it uses JNI (java native interface) to bridge the box-2d implementation from C to Java
    }
}
