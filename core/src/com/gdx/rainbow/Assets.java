package com.gdx.rainbow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by WAM on 9/5/2016.
 */
public class Assets {

    public static Texture background;
    public static Texture player;

    public static Texture loadTexture (String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public static void load() {
        background = loadTexture("data/background.png");
        player = loadTexture("data/player.png");
    }

    public static void playSound (Sound sound) {
        sound.play(1);
    }

    public static void dispose() {
        background.dispose();
        player.dispose();
    }

}
