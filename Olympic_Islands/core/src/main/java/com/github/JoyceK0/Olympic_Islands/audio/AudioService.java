package com.github.JoyceK0.Olympic_Islands.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;
import com.github.JoyceK0.Olympic_Islands.asset.MusicAsset;
import com.github.JoyceK0.Olympic_Islands.asset.SoundAsset;

public class AudioService {

    private final AssetService assetService;
    private Music currentMusic;
    private MusicAsset currentMusicAsset;
    private float musicVolume;
    private float soundVolume;

    public AudioService(AssetService assetService) {
        this.assetService = assetService;
        this.currentMusic = null;
        this.currentMusicAsset = null;
        this.musicVolume = 0.5f; // value between 0 and 1 for volume, keeping at half of original volume
        this.soundVolume = 0.33f; // A third of original volume
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = MathUtils.clamp(musicVolume, 0f, 1f); // restrain between required values
        if(this.currentMusic != null) {
            this.currentMusic.setVolume(musicVolume);
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = MathUtils.clamp(soundVolume, 0f, 1f);
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void playMusic(MusicAsset musicAsset) {
        if(this.currentMusicAsset == musicAsset) return; // music is already playing, no difference in screen

        if(this.currentMusic!=null) { // if something else playing, stop , forget, and then play new file
            this.currentMusic.stop();
            this.assetService.unload(this.currentMusicAsset); // remove old music file from temporary memory and free up space
        }

        this.currentMusic = this.assetService.load(musicAsset);
        this.currentMusic.setLooping(true); // keep looping background music
        this.currentMusic.setVolume(musicVolume);
        this.currentMusic.play(); // can also pause and/or start playing from a certain timestamp
        //this.currentMusic.setOnCompletionListener(); --> can make some action be performed after music stops playing
        this.currentMusicAsset = musicAsset;

    }

    public void playSound(SoundAsset soundAsset) {
        this.assetService.get(soundAsset).play(soundVolume); // since all sound effects are loaded in loading screen, simple access the sound asset from asset service and play it
        // for this one you can also set pitch, which helps make the sound effect a bit different every time and reduces repetitiveness
    }

    public void setMap(TiledMap tiledMap) {
        String musicAssetStr = tiledMap.getProperties().get("music", "", String.class);
        if(musicAssetStr.isBlank()) return; // no asset to play

        MusicAsset musicAsset = MusicAsset.valueOf(musicAssetStr); // sets music asset as the enum with teh constant name similar to the musicAssetStr
        playMusic(musicAsset);
    }
}
