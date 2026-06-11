package com.github.JoyceK0.Olympic_Islands.utils;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerTool {

    public static void main(String[] args) {

        String inputDir = "Olympic_Islands/assets_raw/objects";
        String outputDir = "Olympic_Islands/assets/graphics";
        String packFileName = "objects";

        TexturePacker.process(inputDir, outputDir, packFileName);

    }

}
