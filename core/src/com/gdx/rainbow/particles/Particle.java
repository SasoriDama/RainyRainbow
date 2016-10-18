package com.gdx.rainbow.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by WAM on 9/13/2016.
 */
public class Particle {

    public Vector2 position, velocity, acceleration;

    private Color color;

    public float timer = 0;
    private float life;
    private float angleOffset;
    private float angleInDegreesToXAxis;

    public Particle(Vector2 position, Vector2 velocity, Vector2 acceleration, Color color, float angleOffset, float life) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.color = color;
        this.angleOffset = angleOffset;
        this.life = life;
    }

    public void update(float delta) {

        if (life == -52) System.out.println(position);

        timer += delta;

        position.x += velocity.x * delta + acceleration.x * .5f * delta * delta;
        position.y += velocity.y * delta + acceleration.y * .5f * delta * delta;

        velocity.x += acceleration.x;
        velocity.y += acceleration.y;


        angleInDegreesToXAxis = 180/MathUtils.PI * (float) Math.acos(velocity.cpy().nor().dot(1, 0));
        if (velocity.y > 0) angleInDegreesToXAxis = -angleInDegreesToXAxis;

    }

    public void draw(SpriteBatch batch, Texture t, float xScale, float yScale, boolean fadeIn, float fadeInTime, float endSize) {

        TextureRegion r = new TextureRegion(t);
        //batch.setColor(1, 1, 1, .8f);
        float aAdjst = 1;
        if (!fadeIn) {
            if (life > 0) aAdjst = 1f - (timer / life);
        }
        if (fadeIn) {
            if (life > 0) {
                    if (timer/life < fadeInTime) aAdjst = (timer/life);
                    if (timer/life >= fadeInTime) aAdjst = 1f - (timer / life);
            }
        }

        float rWidth = r.getRegionWidth();
        float rHeight = r.getRegionHeight();
        float x = position.x - rWidth/2;
        float y = position.y - rHeight/2;

        if (endSize != 1) {
            xScale *= (timer/life) * endSize;
            yScale *= (timer/life) * endSize;
        }

        batch.setColor(color.r, color.g, color.b, color.a * (aAdjst));
        //batch.draw(r, position.x - xScale/2, position.y - yScale/2, xScale, yScale);
        //batch.draw(r, position.x, position.y, r.getRegionWidth()/2, r.getRegionHeight()/2, r.getRegionWidth(), r.getRegionHeight(), xScale, yScale, 0);
        batch.draw(r, x, y, rWidth/2, rHeight/2, rWidth, rHeight, xScale/rWidth, yScale/rHeight, (this.angleOffset) - this.angleInDegreesToXAxis);
        batch.setColor(1, 1, 1, 1);
    }


}
