package com.github.JoyceK0.Olympic_Islands.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;
import com.github.JoyceK0.Olympic_Islands.asset.MapAsset;

import java.util.function.Consumer;

public class TiledService {

    private final AssetService assetService;
    private TiledMap currentMap;

    // The tutorial uses consumer systems rather than a traditional object listeners. Object listeners is an OOP
    // architecture which is basically a piece of code that reacts to a certain action, like is a user clicks in
    // a certain spot on the screen such as a button, the code reacts according to that specific interaction.
    // Consumer systems or entity component systems are systems where the game data and the game logic is separate, unlike
    // Object listeners. So instead of listening for events and specific interactions, the system processes logic
    // continuously in the main game render() loop. This is great for scalability and increasing complexity. This uses Ashley.
    private Consumer<TiledMap> mapChangeConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectConsumer;

    public TiledService(AssetService assetService) { // Constructor, sets up default values for all attributes
        this.assetService = assetService;
        this.mapChangeConsumer = null;
        this.loadObjectConsumer = null;
        this.currentMap = null;
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
            }
        }
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

}
