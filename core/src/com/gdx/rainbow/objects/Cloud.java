package com.gdx.rainbow.objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
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
    public float justSpawned = 1;

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
        float sunScale = .8f + ((.3f + (sunTimer * .01f)) * Math.abs(MathUtils.sin(GameScreen.ELAPSED_TIME *.9f)));
        float sunX =  body.getPosition().x - sunSize*sunScale/2;
        float sunY = body.getPosition().y - sunSize*sunScale/2;
        float yOff = .1f;
        float initialAngleOffsetInDegrees = 36;

        Color c1 = new Color((float)255/255, (float)120/255, (float)60/255, 1);
        Color c2 = new Color((float)250/255, (float)176/255, (float)78/255, 1);

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
            if (i % 2 == 0) batch.setColor(c2);
            else batch.setColor(c1);
            batch.draw(t, x, y + yOff, t.getRegionWidth()/2, t.getRegionHeight()/2, t.getRegionWidth(), t.getRegionHeight(), sunTickSize * scl, sunTickSize * scl, per * 360 - 90 - initialAngleOffsetInDegrees);
            batch.setColor(1, 1, 1, 1);
        }

        batch.draw(r,  body.getPosition().x -r.getRegionWidth()/2,  body.getPosition().y -r.getRegionHeight()/2 + yOff, r.getRegionWidth()/2, r.getRegionHeight()/2, r.getRegionWidth(), r.getRegionHeight(), sunSize * sunScale * .0045f, sunSize * sunScale * .0045f,
                ((sunTimer/1)/GameScreen.CLOUD_WIN_TIME) * 360);
    }

    public void drawRainbow(SpriteBatch batch, float amt) {

        TextureRegion t = new TextureRegion(Assets.rainbow_band_image);
        float tWidth = t.getRegionWidth();
        float tHeight = t.getRegionHeight();

        float xScale = .0002f;
        float yScale = .0025f;

        float centerX = 0;
        float centerY = 0 - GameScreen.UNIT_HEIGHT/2;
        //float centerY = body.getPosition().y - GameScreen.UNIT_HEIGHT/2;

        float tempX = body.getPosition().x - centerX;
        float tempY = body.getPosition().y - centerY;

        float eccentricity = 1.3f;
        float transparency = .45f;

        //dist from cloud to bottom center
        float radius = (float) Math.sqrt(tempX * tempX + tempY * tempY);

        //float aSqrd = (radius * eccentricity) * (radius * eccentricity);
        //float bSqrd = (radius * radius);
        //float circumference = (float) (MathUtils.PI2 * Math.sqrt( ((aSqrd) + (bSqrd))/2 ));
        float circumference = MathUtils.PI * 2 * radius;


        float percentOfCircle = .5f;
        percentOfCircle = .15f;
        percentOfCircle *= amt;
        float resolution = circumference  * percentOfCircle/(t.getRegionWidth() * xScale);
        resolution *= 2f;

        Vector2 pos = new Vector2(body.getPosition().x - centerX, body.getPosition().y - centerY);
        pos.nor();
        Vector2 axis = new Vector2(1, 0);
        axis.nor();
        //so it starts at cloud
        float angleBetweenCloudAndXAxisInRadians = (float) Math.acos(pos.dot(axis));
        float angleBetweenCloudAndXAxisInDegrees = angleBetweenCloudAndXAxisInRadians * 180/MathUtils.PI;


        for (int i = 0; i < resolution; i ++) {
            float x = centerX - tWidth/2;
            float y = centerY - tHeight/2;

            //try parabola
            float percent = (float) i/resolution;

            float dx1 = radius * eccentricity *  MathUtils.cos(angleBetweenCloudAndXAxisInRadians + (percent * MathUtils.PI * percentOfCircle));
            float dy1 =  radius * MathUtils.sin(angleBetweenCloudAndXAxisInRadians + (percent * MathUtils.PI * percentOfCircle));

            float dx2 = radius * eccentricity * MathUtils.cos(angleBetweenCloudAndXAxisInRadians - (percent * MathUtils.PI * percentOfCircle));
            float dy2 =  radius * MathUtils.sin(angleBetweenCloudAndXAxisInRadians - (percent * MathUtils.PI * percentOfCircle));
            //batch.setColor(1, 1, 1, .6f);
            batch.setColor(1, 1, 1, transparency * (1 - percent));
            batch.draw(t, x + dx1, y + dy1, tWidth/2, tHeight/2, tWidth, tHeight, xScale, yScale, percent * (180 * percentOfCircle) - (90 - angleBetweenCloudAndXAxisInDegrees));
            batch.draw(t, x + dx2, y + dy2, tWidth/2, tHeight/2, tWidth, tHeight, xScale, yScale, -percent * (180 * percentOfCircle) - (90 - angleBetweenCloudAndXAxisInDegrees));
        }
        batch.draw(t, centerX, centerY, tWidth/2, tHeight/2, tWidth, tHeight, xScale, yScale, 0);
        batch.setColor(1, 1, 1, 1);



    }

    private Color lerpColor(Color startColor, Color endColor, float per) {
        Color c = new Color();
        c.r = startColor.r + lerp(startColor.r, endColor.r, per);
        c.g = startColor.g + lerp(startColor.g, endColor.g, per);
        c.b = startColor.b + lerp(startColor.b, endColor.b, per);
        c.a = .26f;
        return c;
    }

    private float lerp(float start, float end, float per) {
        return (end - start) * per;
    }

    protected void configBodyDef() {
        bodyDef.type = BodyType.DynamicBody;
    }

    protected void configFixtureDef() {
        PolygonShape box = new PolygonShape();
        box.setAsBox(WIDTH, HEIGHT);

        fixtureDef.shape = box;
        fixtureDef.density = .85f;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = Object.CATEGORY_CLOUD;
        fixtureDef.filter.maskBits = Object.MASK_CLOUD;
    }

}
