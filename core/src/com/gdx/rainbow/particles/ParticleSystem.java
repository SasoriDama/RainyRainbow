package com.gdx.rainbow.particles;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.rainbow.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gdx.rainbow.screens.GameScreen;


import java.util.ArrayList;

/**
 * Created by WAM on 9/13/2016.
 */
public class ParticleSystem {

    public ArrayList<Particle> particles, removedParticles;

    public ParticleSystemInfo info;

    public float nextParticleTimer = 0;

    public Vector2 position;

    private float specialDelayTimer = 0;
    private float specialDelayTime = 0;

    private Vector2 offSet, intiialVelocity, initialAcceleration;
    public Color colorOffset = new Color(1, 1, 1, 1);


    public ParticleSystem(ParticleSystemInfo info) {
        this.info = info;
        position = new Vector2(0, 0);
        particles = new ArrayList<Particle>();
        removedParticles = new ArrayList<Particle>();
    }

    public ParticleSystem(ParticleSystemInfo info, Vector2 position) {
        this.info = info;
        this.position = position;
        particles = new ArrayList<Particle>();
        removedParticles = new ArrayList<Particle>();
    }

    public void clear() {
        for (Particle p: particles) {
            removedParticles.add(p);
        }
    }

    public void createParticleWithDelay(float delay, Vector2 offSet, Color colorOffset, Vector2 intiialVelocity, Vector2 initialAcceleration) {
        this.specialDelayTime = delay;
        this.offSet = offSet;
        this.intiialVelocity = intiialVelocity;
        this.initialAcceleration = initialAcceleration;
        if (colorOffset != null) this.colorOffset = colorOffset;
    }

    public void createParticleWithDelay(float delay, Vector2 offSet, Vector2 intiialVelocity, Vector2 initialAcceleration) {
        this.createParticleWithDelay(delay, offSet, null, intiialVelocity, initialAcceleration);
    }

    public void update(float delta, int particlesPerFrame) {

        if (info.nextParticleTime >= 0) nextParticleTimer += delta;

        if (specialDelayTime != 0) {
            this.specialDelayTimer += delta;
            if (specialDelayTimer >= specialDelayTime) {
                specialDelayTimer = 0;
                specialDelayTime = 0;
                this.addParticle(offSet, intiialVelocity, initialAcceleration);
            }
        }

        if (info.nextParticleTime > 0) {
            if (nextParticleTimer >= info.nextParticleTime) {
                nextParticleTimer = 0;
                for (int i = 0; i < particlesPerFrame; i++) {
                    addParticle();
                }
            }
        }

        for (Particle p: particles) {
            p.update(delta);
            if (p.position.x < -GameScreen.UNIT_WIDTH/2 || p.position.x > GameScreen.UNIT_WIDTH/2 || p.position.y < -GameScreen.UNIT_HEIGHT/2) {
                if (info.life <= 0) removedParticles.add(p);
                else if (p.timer >= info.life) removedParticles.add(p);
            }

            //if particle info specifies that particles should despawn on their own then despawn them
            if (info.life > 0) if (p.timer >= info.life) removedParticles.add(p);

        }

        for (Particle p: removedParticles) {
            particles.remove(p);
        }

        removedParticles.clear();
    }

    public void addParticle(Vector2 offSet, Vector2 initialVelocity, Vector2 initialAccleration) {

        Color c = new Color();
        c.set(info.color.r * colorOffset.r, info.color.g * colorOffset.g, info.color.b * colorOffset.b, info.color.a * colorOffset.a);

        if (info.maxParticles > 0) if (particles.size() >= info.maxParticles) return;

        particles.add(new Particle(this.position.cpy().add(offSet.cpy()), initialVelocity, initialAccleration, c, info.angleOffset, info.life));

    }

    public void addParticle() {

        Color c = new Color();
        c.set(info.color.r * colorOffset.r, info.color.g * colorOffset.g, info.color.b * colorOffset.b, info.color.a * colorOffset.a);

        if (info.maxParticles > 0) if (particles.size() >= info.maxParticles) return;

        Vector2 position = new Vector2();
        Vector2 velocity = new Vector2();
        Vector2 acceleration = new Vector2();

        if (info.position != null) position.set(info.position);
        position.x += this.position.x;
        position.y += this.position.y;

        if (info.initialVelocity != null) velocity.set(info.initialVelocity);
        if (info.initialAccel != null) acceleration.set(info.initialAccel);

        float x = info.xMin;
        float y = info.yMin;
        if (info.position == null) {
            if (info.xMin != info.xMax) x = MathUtils.random(info.xMin, info.xMax);
            if (info.yMin != info.yMax) y = MathUtils.random(info.yMin, info.yMax);
            position = new Vector2(x + this.position.x, y + this.position.y);
        }
        if (info.initialVelocity== null) {
           velocity = new Vector2(0, 0);
        }
        if (info.initialAccel == null) {
            acceleration = new Vector2(0, 0);
        }


        particles.add(new Particle(position, velocity, acceleration, c, info.angleOffset, info.life));
    }

    public void drawParticles(SpriteBatch batch) {

        for (Particle p: particles) {
            p.draw(batch, info.texture, info.xScale, info.yScale, info.fadeIn, info.fadeInTime, info.endSize);
        }
    }


}
