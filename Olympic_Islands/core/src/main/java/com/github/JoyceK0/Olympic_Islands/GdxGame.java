package com.github.JoyceK0.Olympic_Islands;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;

import java.util.HashMap;
import java.util.Map;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GdxGame extends Game {
    public static  final float WORLD_WIDTH = 16f;
    public static final float WORLD_HEIGHT = 9f;
    public static final float UNIT_SCALE = 1f / 16f;

    private Batch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private AssetService assetService;
    private GLProfiler glProfiler;
    private FPSLogger fpsLogger;

    private final Map<Class<? extends Screen>, Screen> screenCache = new HashMap<>();

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG); // shows all debug messages

        this.batch = new SpriteBatch(); // renders graphics to screen
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.assetService = new AssetService(new InternalFileHandleResolver());
        this.glProfiler = new GLProfiler(Gdx.graphics);
        this.glProfiler.enable();
        this.fpsLogger = new FPSLogger();

        addScreen(new GameScreen(this));
        setScreen(GameScreen.class);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
        super.resize(width, height);
    }

    public void addScreen(Screen screen) {
        screenCache.put(screen.getClass(),screen);
    }

    public void setScreen(Class<? extends Screen> screenClass) {
        Screen screen = screenCache.get(screenClass);
        if(screen == null){
            throw new GdxRuntimeException("No screen with this class " + screenClass + " found in the screen cache");
        }
        super.setScreen(screen);
    }

    @Override
    public void render() {
        glProfiler.reset();

        Gdx.gl.glClearColor(0f,0f, 0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();

        Gdx.graphics.setTitle("Olympic Games - Draw Calls: " + glProfiler.getDrawCalls()); // names windows, shows draw calls. minimum draw calls is best.
        fpsLogger.log(); // every 1s it will print the fps to console
    }

    @Override
    public void dispose() {
        screenCache.values().forEach(Screen::dispose);
        screenCache.clear();

        this.batch.dispose();
        this.assetService.debugDiagnostics();
        this.assetService.dispose();
    }
    public Batch getBatch() {
        return batch;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
