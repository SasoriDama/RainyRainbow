package com.gdx.rainbow.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.GameScreen;
import com.gdx.rainbow.MyGdxGame;

/**
 * Created by WAM on 9/13/2016.
 */
public class Particle {

    public Vector2 position, velocity, acceleration;
    public float size = .3f;

    public Particle(Vector2 position, Vector2 velocity, Vector2 acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public void update(float delta) {

        position.x += velocity.x * delta + acceleration.x * .5f * delta * delta;
        position.y += velocity.y * delta + acceleration.y * .5f * delta * delta;

        velocity.x += acceleration.x;
        velocity.y += acceleration.y;

    }

    public void draw(SpriteBatch batch) {
        TextureRegion r = new TextureRegion(Assets.sun_timer_tick_image);
        batch.setColor(1, 1, 1, .8f);
        batch.draw(r, position.x, position.y, .006f, .5f);
        batch.setColor(1, 1, 1, 1);
    }

    public void drawTwo(ShapeRenderer sr) {
        sr.line(position.x, position.y, position.x + .02f, position.y - size);
    }
}
