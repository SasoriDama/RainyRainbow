package com.gdx.rainbow.particles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.rainbow.GameScreen;
import com.gdx.rainbow.MyGdxGame;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by WAM on 9/13/2016.
 */
public class ParticleSystem {

    private ArrayList<Particle> particles, removedParticles;

    private float nextParticleTimer = 0;
    private float nextParticleTime;

    public ParticleSystem(float nextParticleTime) {
        particles = new ArrayList<Particle>();
        removedParticles = new ArrayList<Particle>();
        this.nextParticleTime = nextParticleTime;
    }

    public void update(float delta, int particlesPerFrame) {

        nextParticleTimer += delta;

        if (nextParticleTimer >= nextParticleTime) {
            nextParticleTimer = 0;
            for (int i = 0; i < particlesPerFrame; i++) {
                addParticle();
            }
        }

        for (Particle p: particles) {
            p.update(delta);
            if (p.position.x < -GameScreen.UNIT_WIDTH/2 || p.position.x > GameScreen.UNIT_WIDTH/2 || p.position.y < -GameScreen.UNIT_HEIGHT/2) {
                removedParticles.add(p);
            }
        }

        for (Particle p: removedParticles) {
            particles.remove(p);
        }

        removedParticles.clear();
    }

    private void addParticle() {
        float x = MathUtils.random((float)-GameScreen.UNIT_WIDTH/2, (float)GameScreen.UNIT_WIDTH/2);
        float y = GameScreen.UNIT_HEIGHT/2 + .3f;

        Vector2 newPosition = new Vector2(x, y);
        Vector2 newVelocity = new Vector2(.2f, -1.5f);
        Vector2 newAcceleration = new Vector2(0, -1);

        particles.add(new Particle(newPosition, newVelocity, newAcceleration));
    }

    public void drawParticles(SpriteBatch batch) {

        for (Particle p: particles) {
            p.draw(batch);
        }
    }

    public void drawParticlesTwo(ShapeRenderer sr) {
        sr.setColor(Color.WHITE);
        for (Particle p: particles) {
            p.drawTwo(sr);
        }
    }


}
