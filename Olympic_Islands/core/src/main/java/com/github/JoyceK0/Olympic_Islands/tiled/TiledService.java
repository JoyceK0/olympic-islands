package com.github.JoyceK0.Olympic_Islands.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;
import com.github.JoyceK0.Olympic_Islands.asset.MapAsset;

import java.util.function.Consumer;

public class TiledService {

    private final AssetService assetService;
    private final World physicWorld;

    private TiledMap currentMap;

    // The tutorial uses consumer systems rather than a traditional object listeners. Object listeners is an OOP
    // architecture which is basically a piece of code that reacts to a certain action, like is a user clicks in
    // a certain spot on the screen such as a button, the code reacts according to that specific interaction.
    // Consumer systems or entity component systems are systems where the game data and the game logic is separate, unlike
    // Object listeners. So instead of listening for events and specific interactions, the system processes logic
    // continuously in the main game render() loop. This is great for scalability and increasing complexity. This uses Ashley.
    private Consumer<TiledMap> mapChangeConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectConsumer;
    private LoadTileConsumer loadTileConsumer;

    public TiledService(AssetService assetService, World physicWorld) { // Constructor, sets up default values for all attributes
        this.assetService = assetService;
        this.mapChangeConsumer = null;
        this.loadObjectConsumer = null;
        this.currentMap = null;
        this.loadTileConsumer = null;
        this.physicWorld = physicWorld;
    }

    public TiledMap loadMap(MapAsset mapAsset) { // this loads the tile map file from its destination using assetService and loads it
        TiledMap tiledMap = this.assetService.load(MapAsset.MAIN);
        tiledMap.getProperties().put("mapAsset", mapAsset);
        return tiledMap;
    }

    public void setMap(TiledMap map){ // sets up the tiled map and ensures it is a valid map, not a null value before assignment
        if(this.currentMap != null) {
            this.assetService.unload(this.currentMap.getProperties().get("mapAsset", MapAsset.class));
        }

        this.currentMap = map;
        loadMapObjects(map);
        if(this.mapChangeConsumer != null) {
            this.mapChangeConsumer.accept(map);
        }
    }

    private void loadMapObjects(TiledMap tiledMap) { // searches through the layers in the tile map until it finds the 'objects' layer. Then it loads that layer
        for(MapLayer layer : tiledMap.getLayers()) {
            if("objects".equals(layer.getName())) {
                loadObjectLayer(layer);
            } else if (layer instanceof TiledMapTileLayer tileLayer){
                loadTileLayer(tileLayer);
            }
        }

        spawnMapBoundary(tiledMap);
    }

    private void loadTileLayer(TiledMapTileLayer tileLayer) {

        if(loadTileConsumer == null) return; // don't need to do anything if consumer not specified

        for(int y = 0; y < tileLayer.getHeight(); y++) {
            for(int x = 0; x < tileLayer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y); // find the tile and its x and y coordinates
                if(cell==null) continue; // no action required
                loadTileConsumer.accept(cell.getTile(), x, y);
            }
        }

    }

    private void spawnMapBoundary(TiledMap tiledMap) {
        Integer width = tiledMap.getProperties().get("width", 0, Integer.class);
        Integer tileW = tiledMap.getProperties().get("tilewidth", 0, Integer.class);
        Integer height = tiledMap.getProperties().get("height", 0, Integer.class);
        Integer tileH = tiledMap.getProperties().get("tileheight", 0, Integer.class);
        float mapW = width*tileW* GdxGame.UNIT_SCALE;
        float mapH = height*tileH*GdxGame.UNIT_SCALE;
        float halfW = mapW*0.5f;
        float halfH = mapH*0.5f;
        float boxThickness = 0.5f;

        BodyDef bodyDef =new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.setZero();
        bodyDef.fixedRotation = true;
        Body body = physicWorld.createBody(bodyDef);
        body.setUserData("environment");

        //left edge
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxThickness, halfH, new Vector2(-boxThickness, halfH),0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();

        //right edge
        shape = new PolygonShape();
        shape.setAsBox(boxThickness, halfH, new Vector2(mapW+boxThickness, halfH),0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();

        //bottom edge
        shape = new PolygonShape();
        shape.setAsBox(halfW, boxThickness, new Vector2(halfW, -boxThickness),0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();

        //top edge
        shape = new PolygonShape();
        shape.setAsBox(halfW, boxThickness, new Vector2(halfW, mapH+boxThickness),0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();
    }

    private void loadObjectLayer(MapLayer objectLayer) { // This takes the object layer from loadMapObjects and extracts all the objects as long as they belong to the tiled map definition of map objects. If not then it throws an error of unsupported type for that variable.
        if(loadObjectConsumer == null) return;

        for(MapObject mapObject : objectLayer.getObjects()) {
            if(mapObject instanceof TiledMapTileMapObject tileMapObject) {
                loadObjectConsumer.accept(tileMapObject);
            } else {
                throw new GdxRuntimeException("Unsupported Object: " + mapObject.getClass().getSimpleName());
            }
        }

    }

    public void setMapChangeConsumer(Consumer<TiledMap> mapChangeConsumer) {
        this.mapChangeConsumer = mapChangeConsumer;
    }

    public void setLoadObjectConsumer(Consumer<TiledMapTileMapObject> loadObjectConsumer) {
        this.loadObjectConsumer = loadObjectConsumer;
    }

    public void setLoadTileConsumer(LoadTileConsumer loadTileConsumer) {
        this.loadTileConsumer = loadTileConsumer;
    }

    @FunctionalInterface
    public interface LoadTileConsumer {
        void accept(TiledMapTile tile, float x, float y);
    }

}
