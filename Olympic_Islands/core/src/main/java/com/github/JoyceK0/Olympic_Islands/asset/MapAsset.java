package com.github.JoyceK0.Olympic_Islands.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public enum MapAsset implements Asset<TiledMap>{
    MAIN("main.tmx"); //check if file is called that || it is called that

    private final AssetDescriptor<TiledMap> descriptor;

     MapAsset(String mapName){
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.projectFilePath = "maps/Tiled.tiled-project";
        this.descriptor = new AssetDescriptor<>("maps/"+mapName, TiledMap.class, parameters); //check if folder is called that
    }
    @Override
    public AssetDescriptor<TiledMap> getDescriptor() {
        return this.descriptor;
    }
}
