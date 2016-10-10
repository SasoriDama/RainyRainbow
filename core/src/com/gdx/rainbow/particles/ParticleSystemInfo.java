package com.gdx.rainbow.particles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.GameScreen;

/**
 * Created by WAM on 10/3/2016.
 */
public class ParticleSystemInfo {

    public static final ParticleSystemInfo RAIN = new ParticleSystemInfo(Assets.sun_timer_tick_image, new Color(.94f, 1f, 1, .8f), -GameScreen.UNIT_WIDTH/2, GameScreen.UNIT_WIDTH/2,
            GameScreen.UNIT_HEIGHT / 2 + .3f, GameScreen.UNIT_HEIGHT / 2 + .3f, 0.01f, null, new Vector2(.11f, -1.5f), new Vector2(0, -1f), .006f, .5f);

    public static final ParticleSystemInfo BLOW = new ParticleSystemInfo(Assets.blow_air_image, new Color(1f, 1f, 1f, 1f), null, null, null, .125f * 1.5f, .125f * 1.5f * .5f, 1f, 0, 0);

    public static final ParticleSystemInfo STORM_CLOUDS = new ParticleSystemInfo(Assets.storm_cloud_particle, new Color(1f, 1f, 1f, .7f), -.75f, .75f, -.75f, .75f, .0000001f, null, null, null, .75f * 1.4f, .75f * 1.4f, 10, 0, 0, true, .5f);

    public static final ParticleSystemInfo PLAYER_LIGHTNING_TRAIL = new ParticleSystemInfo(Assets.lightning_bolt_image, new Color(1f, 1f, .7f, .55f), 0, 0, 0, 0, 0, null, null, null, .075f * .5f * 12, .075f * 2.5f * 2, 1.2f/4f, 0, 90, true, .5f);

    public static final ParticleSystemInfo PLAYER_RAINBOW_TRAIL = new ParticleSystemInfo(Assets.rainbow_band_image, new Color(1f, 1f, 1f, .25f), 0, 0, 0, 0, 0, null, null, null, .25f, .25f, .6f, 0, 90, false, .5f);

    public Texture texture;
    public Color color;
    //potential spawning locations of particles
    public float xMin, xMax, yMin, yMax;
    public float nextParticleTime;
    public Vector2 position;
    public Vector2 initialAccel, initialVelocity;
    public float xScale, yScale;
    public float life;
    public int maxParticles;
    public float angleOffset;
    public boolean fadeIn;
    public float fadeInTime = .5f;
    public float endSize = 1;

    public ParticleSystemInfo(Texture texture, Color color, float xMin, float xMax, float yMin, float yMax, float nextParticleTime, Vector2 position, Vector2 initialAccel, Vector2 initialVelocity, float xScale, float yScale, float life, int maxParticles, float angleOffset, boolean fadeIn, float fadeInTime) {
        this.texture = texture;
        this.color = color;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.nextParticleTime = nextParticleTime;
        this.position = position;
        this.initialAccel = initialAccel;
        this.initialVelocity = initialVelocity;
        this.xScale = xScale;
        this.yScale = yScale;
        this.life = life;
        this.maxParticles = maxParticles;
        this.angleOffset = angleOffset;
        this.fadeIn = fadeIn;
        this.fadeInTime = fadeInTime;
    }

    public ParticleSystemInfo(Texture texture, Color color, float xMin, float xMax, float yMin, float yMax, float nextParticleTime, Vector2 position, Vector2 initialAccel, Vector2 initialVelocity, float xScale, float yScale) {
        this.texture = texture;
        this.color = color;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.nextParticleTime = nextParticleTime;
        this.position = position;
        this.initialAccel = initialAccel;
        this.initialVelocity = initialVelocity;
        this.xScale = xScale;
        this.yScale = yScale;
        this.life = 0;
        this.maxParticles = 0;
        angleOffset = 90;
        this.fadeIn = false;
    }


    public ParticleSystemInfo(Texture texture, Color color, Vector2 position, Vector2 initialAccel, Vector2 initialVelocity, float xScale, float yScale, float life, int maxParticles, float angleOffset) {
        this.texture = texture;
        this.color = color;
        this.xMin = 0;
        this.xMax = 0;
        this.yMin = 0;
        this.yMax = 0;
        this.nextParticleTime = -1;
        this.position = position;
        this.initialAccel = initialAccel;
        this.initialVelocity = initialVelocity;
        this.xScale = xScale;
        this.yScale = yScale;
        this.life = life;
        this.maxParticles = maxParticles;
        this.angleOffset = angleOffset;
        this.fadeIn = false;
    }

}
