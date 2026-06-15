package com.github.JoyceK0.Olympic_Islands.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;
import com.github.JoyceK0.Olympic_Islands.asset.AtlasAsset;
import com.github.JoyceK0.Olympic_Islands.asset.SoundAsset;

public class LoadingScreen extends ScreenAdapter {

    private final GdxGame game;
    private final AssetService assetService;

    public LoadingScreen(GdxGame game, AssetService assetService) {
        this.game = game;
        this.assetService = assetService;
    }

    @Override
    public void show() {
        for (AtlasAsset atlas : AtlasAsset.values()) {
            assetService.queue(atlas);
        }
        for(SoundAsset sound : SoundAsset.values()) {
            assetService.queue(sound); // load all sound effect files at once for efficiency
        }
    }

    @Override
    public void render(float delta) {
        if(this.assetService.update()) {
            Gdx.app.debug("LoadingScreen", "Finished asset loading"); // print in console when assets have been loaded
            createScreens();
            this.game.removeScreen(this);
            this.dispose();
            this.game.setScreen(GameScreen.class); //switch back to game screen after loading
        }
    }

    private void createScreens() {
        this.game.addScreen(new GameScreen(this.game));
    }
}
