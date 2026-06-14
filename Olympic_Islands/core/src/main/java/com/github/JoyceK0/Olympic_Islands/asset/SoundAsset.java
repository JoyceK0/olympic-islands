package com.github.JoyceK0.Olympic_Islands.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;

public enum SoundAsset implements Asset<Sound> {

    CLICK("click.wav");

    private final AssetDescriptor<Sound> descriptor;

    SoundAsset(String musicFile) {
        this.descriptor = new AssetDescriptor<>("audio/" + musicFile, Sound.class);
    }

    @Override
    public AssetDescriptor<Sound> getDescriptor() {
        return descriptor;
    }

}
