package com.gdx.rainbow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

/**
 * Created by WAM on 9/5/2016.
 */
public class Assets {

    public static final float CLOUD_IMAGE_SCALE = 1.65f;
    public static final float CLOUD_ALPHA = .7f;

    public static Texture background_image;
    public static Texture sun_timer_image;
    public static Texture sun_timer_tick_image;
    public static Texture sun_beam_band_image;
    public static Texture rainbow_band_image;
    public static Texture storm_cloud_particle;
    public static Box2DSprite player_image;
    public static Box2DSprite cloud_image;

    public static AnimatedBox2DSprite player_blowing_animation;
    public static AnimatedBox2DSprite player_blowing_animation_flipped;
    public static AnimatedBox2DSprite player_win_animation;

    public static Texture stats_screen_plus_button;
    public static Texture stats_screen_minus_button;

    public static Texture loadTexture (String file) {
        return new Texture(Gdx.files.internal(file));
    }

    private static Box2DSprite loadBox2DSprite(String file) {
        Texture t = loadTexture(file);
        return new Box2DSprite(t);
    }

    public static void load() {

        background_image = loadTexture("data/backgroundthree.png");
        sun_timer_image = loadTexture("data/sun_timer_five.png");
        sun_timer_tick_image = loadTexture("data/sun_tick_four.png");
        sun_beam_band_image = loadTexture("data/sunbeam_band_two.png");
        rainbow_band_image = loadTexture("data/rainbow_band_two.png");
        storm_cloud_particle = loadTexture("data/storm_cloud_particle.png");
        player_image = loadBox2DSprite("data/player.png");
        cloud_image = loadBox2DSprite("data/cloud_one.png");
        cloud_image.setScale(CLOUD_IMAGE_SCALE);


        Texture temp = loadTexture("data/player_blow_animation.png");
        TextureRegion t1 = new TextureRegion(temp, 0, 0, 256, 256);
        TextureRegion t2 = new TextureRegion(temp, 0, 256, 256, 256);
        TextureRegion t3 = new TextureRegion(temp, 0, 256 * 2, 256, 256);
        TextureRegion t4 = new TextureRegion(temp, 0, 256 * 3, 256, 256);
        TextureRegion t5 = new TextureRegion(temp, 0, 256 * 4, 256, 256);
        Animation a = new Animation(.20f, t1, t2, t3, t4, t5);
        AnimatedSprite as = new AnimatedSprite(a);
        player_blowing_animation = new AnimatedBox2DSprite(as);

        temp = loadTexture("data/player_blow_animation_flipped.png");
        t1 = new TextureRegion(temp, 0, 0, 256, 256);
        t2 = new TextureRegion(temp, 0, 256, 256, 256);
        t3 = new TextureRegion(temp, 0, 256 * 2, 256, 256);
        t4 = new TextureRegion(temp, 0, 256 * 3, 256, 256);
        t5 = new TextureRegion(temp, 0, 256 * 4, 256, 256);
        a = new Animation(.20f, t1, t2, t3, t4, t5);
        as = new AnimatedSprite(a);
        player_blowing_animation_flipped = new AnimatedBox2DSprite(as);

        temp = loadTexture("data/player_win_animation.png");
        t1 = new TextureRegion(temp, 0, 0, 256, 256);
        t2 = new TextureRegion(temp, 0, 256, 256, 256);
        t3 = new TextureRegion(temp, 0, 256 * 2, 256, 256);
        t4 = new TextureRegion(temp, 0, 256 * 3, 256, 256);
        t5 = new TextureRegion(temp, 0, 256 * 4, 256, 256);
        a = new Animation(.5f, t1, t2, t3, t4, t5);
        as = new AnimatedSprite(a);
        player_win_animation = new AnimatedBox2DSprite(as);

        stats_screen_plus_button = loadTexture("data/stats_screen/plus_button.png");
        stats_screen_minus_button = loadTexture("data/stats_screen/minus_button.png");
    }

    public static void playSound (Sound sound) {
        sound.play(1);
    }

    public static void dispose() {
        background_image.dispose();
    }

}
