package com.gdx.rainbow.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.GameScreen;

/**
 * Created by WAM on 9/5/2016.
 */
public class Cloud extends Object {

    public float sunTimer = 0;

    //public static final float WIDTH = .768f * .98f;
    //public static final float HEIGHT = .356f * .98f;

    public static final float WIDTH = .768f * .85f;
    public static final float HEIGHT = .356f * .85f;

    public Cloud() {
        super();
    }

    public void set(GameScreen gameScreen, float x, float y, Vector2 initialVel) {
        super.set(gameScreen, x, y);

        setSprite(Assets.cloud_image);

        body.setLinearVelocity(initialVel);
    }

    public void drawTimer(SpriteBatch batch) {
        float sunSize = .30f;
        float sunTickSize = .0016f;
        float sunScale = .8f + (.3f * Math.abs(MathUtils.sin(GameScreen.ELAPSED_TIME *.9f)));
        float sunX =  body.getPosition().x - sunSize*sunScale/2;
        float sunY = body.getPosition().y - sunSize*sunScale/2;
        float initialAngleOffsetInDegrees = 36;
        TextureRegion t = new TextureRegion(Assets.sun_timer_tick_image);
        TextureRegion r = new TextureRegion(Assets.sun_timer_image);
        int numOfTicks = 16;
        for (int i = 0; i < numOfTicks; i++) {
            float per = ((float) i/numOfTicks);
            if (per > (sunTimer/GameScreen.CLOUD_WIN_TIME)) continue;
            float x = sunSize*sunScale/2 * ((MathUtils.cos(per * MathUtils.PI2 - (initialAngleOffsetInDegrees * MathUtils.PI/180)))) + (sunX + sunSize*sunScale/2) - t.getRegionWidth()/2;
            float y = sunSize*sunScale/2 * ((MathUtils.sin(per * MathUtils.PI2 - (initialAngleOffsetInDegrees * MathUtils.PI/180)))) + (sunY + sunSize*sunScale/2) - t.getRegionHeight()/2;
            float scl = .4f;
            float dx = (float) ((x - sunX) * .0005f * .85f * Math.sin(GameScreen.ELAPSED_TIME));
            float dy = (float) ((y - sunY) * .0005f * .85f * Math.sin(GameScreen.ELAPSED_TIME));
            dx *= (MathUtils.cos(per * MathUtils.PI2 - (initialAngleOffsetInDegrees * MathUtils.PI/180)));
            dy *= (MathUtils.sin(per * MathUtils.PI2 - (initialAngleOffsetInDegrees * MathUtils.PI/180)));
            x += dx;
            y += dy;
            if (i % 2 == 0) scl *= 1.6f;
            scl *= .65f * (Math.abs(Math.sin(GameScreen.ELAPSED_TIME + i * 10.4f)) + 2.3);
            batch.draw(t, x, y, t.getRegionWidth()/2, t.getRegionHeight()/2, t.getRegionWidth(), t.getRegionHeight(), sunTickSize * scl, sunTickSize * scl, per * 360 - 90 - initialAngleOffsetInDegrees);
        }
        //batch.draw(Assets.sun_timer_image, sunX , sunY, sunSize * sunScale, sunSize * sunScale);
        batch.draw(r,  body.getPosition().x -r.getRegionWidth()/2,  body.getPosition().y -r.getRegionHeight()/2, r.getRegionWidth()/2, r.getRegionHeight()/2, r.getRegionWidth(), r.getRegionHeight(), sunSize * sunScale * .0045f, sunSize * sunScale * .0045f,
                ((sunTimer/1)/GameScreen.CLOUD_WIN_TIME) * 360);
    }

    protected void configBodyDef() {
        bodyDef.type = BodyType.DynamicBody;
    }

    protected void configFixtureDef() {
        PolygonShape box = new PolygonShape();
        //box.setAsBox((768/7), 356/7);
        box.setAsBox(WIDTH, HEIGHT);

        fixtureDef.shape = box;
        fixtureDef.density = 1.35f;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = Object.CATEGORY_CLOUD;
        fixtureDef.filter.maskBits = Object.MASK_CLOUD;
    }

}
