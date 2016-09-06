package com.gdx.rainbow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

/**
 * Created by WAM on 9/5/2016.
 */
public class Assets {

    public static Texture background_image;
    public static Box2DSprite player_image;
    public static Box2DSprite cloud_image;

    public static Texture loadTexture (String file) {
        return new Texture(Gdx.files.internal(file));
    }

    private static Box2DSprite loadBox2DSprite(String file) {
        Texture t = loadTexture(file);
        return new Box2DSprite(t);
    }

    public static void load() {
        background_image = loadTexture("data/background.png");
        player_image = loadBox2DSprite("data/player.png");
        cloud_image = loadBox2DSprite("data/cloudthree.png");
    }

    public static void playSound (Sound sound) {
        sound.play(1);
    }

    public static void dispose() {
        background_image.dispose();
    }

}
