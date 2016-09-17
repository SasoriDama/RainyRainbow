package com.gdx.rainbow.particles;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.rainbow.MyGdxGame;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by WAM on 9/13/2016.
 */
public class ParticleSystem {

    private ArrayList<Particle> particles;

    private float nextParticleTimer = 0;
    private float nextParticleTime;

    public ParticleSystem(float nextParticleTime) {
        particles = new ArrayList<Particle>();

        this.nextParticleTime = nextParticleTime;

    }

    public void update(float delta) {

        nextParticleTimer += delta;

        if (nextParticleTimer >= nextParticleTime) {
            nextParticleTimer = 0;
            addParticle();
        }

        for (Particle p: particles) {
            p.update(delta);
        }
    }

    private void addParticle() {
        float x = MathUtils.random(-MyGdxGame.WIDTH/2, MyGdxGame.WIDTH/2);
        float y = MyGdxGame.HEIGHT/2 + 100;

        Vector2 newPosition = new Vector2(x, y);
        Vector2 newVelocity = new Vector2(10, -1000);
        Vector2 newAcceleration = new Vector2(0, -9);

        particles.add(new Particle(newPosition, newVelocity, newAcceleration));
    }

    public void drawParticles(ShapeRenderer sr) {
        sr.setColor(Color.WHITE);
        for (Particle p: particles) {
            p.draw(sr);
        }
    }


}
