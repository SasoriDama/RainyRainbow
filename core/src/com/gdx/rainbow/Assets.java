package com.gdx.rainbow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    public static Texture lightning_image;
    public static Texture lightning_bolt_image;
    public static Texture blow_air_image;
    public static Box2DSprite player_image;
    public static Box2DSprite player_poweredup_image;
    public static Box2DSprite cloud_image;
    public static Box2DSprite dense_cloud_image;

    public static AnimatedBox2DSprite player_blowing_animation;
    public static AnimatedBox2DSprite player_blowing_animation_flipped;
    public static AnimatedBox2DSprite player_win_animation;

    public static Texture stats_screen_plus_button;
    public static Texture stats_screen_minus_button;

    public static Music blow_sound;
    public static Music rain_sound;
    public static Music thunder_sound;
    public static Music lightning_particle_sound;
    public static Music[] rainbow_sound = new Music[2];

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
        storm_cloud_particle = loadTexture("data/storm_cloud_particle_three.png");
        lightning_image = loadTexture("data/lightning.png");
        lightning_bolt_image = loadTexture("data/lightning_particle.png");
        blow_air_image = loadTexture("data/blow_particle.png");
        player_image = loadBox2DSprite("data/player.png");
        player_poweredup_image = loadBox2DSprite("data/player_powerup_two.png");
        cloud_image = loadBox2DSprite("data/cloud_one.png");
        cloud_image.setScale(CLOUD_IMAGE_SCALE);
        dense_cloud_image = loadBox2DSprite("data/dense_cloud.png");
        dense_cloud_image.setScale(CLOUD_IMAGE_SCALE);

        blow_sound = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/blow_sound_three.wav"));
        rain_sound = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/rain_sound.wav"));
        thunder_sound = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/thunder_one.wav"));
        lightning_particle_sound = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/chirping_lightning.mp3"));
        lightning_particle_sound.setVolume(.25f);
        rainbow_sound[0] = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/rainbow_sound_one.wav"));
        rainbow_sound[1] = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/rainbow_sound_two.wav"));

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

        temp = loadTexture("data/player_win_animation_two.png");
        t1 = new TextureRegion(temp, 0, 0, 256, 256);
        t2 = new TextureRegion(temp, 0, 256, 256, 256);
        t3 = new TextureRegion(temp, 0, 256 * 2, 256, 256);
        t4 = new TextureRegion(temp, 0, 256 * 3, 256, 256);
        t5 = new TextureRegion(temp, 0, 256 * 4, 256, 256);
        TextureRegion t6 = new TextureRegion(temp, 0, 256 * 5, 256, 256);
        TextureRegion t7 = new TextureRegion(temp, 0, 256 * 6, 256, 256);
        a = new Animation(.35f, t1, t2, t3, t4, t5, t6, t7);
        as = new AnimatedSprite(a);
        player_win_animation = new AnimatedBox2DSprite(as);

        stats_screen_plus_button = loadTexture("data/stats_screen/plus_button.png");
        stats_screen_minus_button = loadTexture("data/stats_screen/minus_button.png");
    }

    public static void playSound (Sound sound) {
        sound.play(1);
    }

    public static void playMusic (Music music) {
        if (music.isPlaying()) return;
        music.play();
    }

    public static void dispose() {
        //nothing is disposed!!
        background_image.dispose();
    }

}
