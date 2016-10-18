package com.gdx.rainbow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by WAM on 9/5/2016.
 */
public class Sunbeam {

    public float xStart, xCenterTop, xCenterBottom;
    private float width, height;
    public float widthScl = 1;
    public Polygon shape;

    //does the sunbeam rotate or move straight if true it moves straight
    public boolean typeOfMovement = true;

    //private float drawLineSpacing = .125f/20;

    private float angleToXDestInDegrees = 0;

    public float xDest, nextXDest;

    public float speed = 1;

    public Sunbeam(float widthScl) {
        this.widthScl = widthScl;
        shape = new Polygon();
        width = 4f * (.2f);
        width *= 1.5f;
        width *= widthScl;
        height = com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT;
        xStart = -width/2;
        setVertices();
    }

    private float calcDx(float xDest) {
        //x comp of vector between center and newX
        float dxx = (xDest - xCenterBottom);

        dxx *= .09f;
        dxx *= 2f;
        dxx *= speed;
        return dxx;
    }

    public boolean readyForNextLocation() {
        return (Math.abs(xCenterBottom - xDest) < .2f);
    }

    private void setVertices() {
        float[] vertices = new float[8];
        //First Vertex Top Left
        vertices[0] = xCenterTop - width/2;
        vertices[1] = com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2;
        //Second Vertex Bottom Left
        vertices[2] = xCenterTop - width/2 - width/3;
        vertices[3] = com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2 - height;
        //Third Vertex Bottom Right
        vertices[4] = xCenterTop + width/2 + width/3;
        vertices[5] = com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2 - height;
        //Fourth Vertex Top Right
        vertices[6] = xCenterTop + width/2;
        vertices[7] = com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2;
        shape.setVertices(vertices);
    }

    private void pushVertices(float dx, float delta) {
        float[] vertices = shape.getVertices();

        //Second Vertex Bottom Left xCoord
        vertices[2] += dx * delta;
        //Third Vertex Bottom Right xCoord
        vertices[4] += dx * delta;

        xCenterBottom = (vertices[2] + vertices[4]) * .5f;

        shape.setVertices(vertices);
    }

    private void pushBeam(float dx, float delta) {
        float[] vertices = shape.getVertices();

        for (int i = 0; i < vertices.length; i++) {
            if (i % 2 != 0) continue;
            vertices[i] += dx * delta;
        }

        shape.setVertices(vertices);

        xCenterTop = (vertices[0] + vertices[6]) * .5f;
        xCenterBottom = (vertices[2] + vertices[4]) * .5f;
    }

    public void update(float xDest, float delta) {
        float dx = calcDx(xDest);
        this.xDest = xDest;
        if (typeOfMovement) {
            pushVertices(dx, delta);

            //Only recalculate angle if the beam is doing rotational movement
            Vector2 start = new Vector2(xCenterBottom - xCenterTop, com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT / 2 - -com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT / 2);
            start.nor();
            Vector2 end = new Vector2(xCenterBottom + dx * delta - xCenterTop, com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT / 2 - -com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT / 2);
            end.nor();
            float dTheta = (180 / MathUtils.PI) * (float) Math.acos((start.dot(end)));
            float dir = 1;
            if (xCenterBottom - xDest > 0) dir = -1;
            if (dTheta > .01f) angleToXDestInDegrees += dir * dTheta;

        } else pushBeam(dx, delta);

    }

    public void draw(SpriteBatch batch, float ALPHA) {

        TextureRegion t = new TextureRegion(Assets.sun_beam_band_image);
        float tWidth = t.getRegionWidth();
        float tHeight = t.getRegionHeight();
        float xScale = .01f;
        xScale = .005f;
        xScale *= widthScl;
        float yScale = .08f;
        float x = xCenterTop;
        float y= com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2 + .2f;
        float a = .45f + .1f * MathUtils.sin(com.gdx.rainbow.screens.GameScreen.ELAPSED_TIME/3);
        a *= .68f;
        a *= ALPHA;
        if (a < 0) a = 0;


        batch.setColor(1, 1, .95f, a * .3f);
        batch.draw(t, x -tWidth/2, y -tHeight/2 * 2f, tWidth/2, 2f * tHeight/2, tWidth, tHeight, xScale, yScale, angleToXDestInDegrees);
        batch.setColor(1, 1, 1, 1);

        float timerDesyncValues[] = {5, 3, 7, 1, 2, 6, 2, 8, 9, 0};
        int numOfExtraBeams = 4;
        numOfExtraBeams = 10;

        //add more glimmer when the sun timer is filling up!
        for (int i = 0; i < numOfExtraBeams; i++) {
            float desyncedTimer = com.gdx.rainbow.screens.GameScreen.ELAPSED_TIME + timerDesyncValues[i];
            float aa = a - (.15f * Math.abs(MathUtils.sin(desyncedTimer/2))) + (.05f * MathUtils.sin(desyncedTimer*2f));
            if (aa < 0) aa = 0;

            float dir = 1;
            if (i % 2 != 0) dir = -1;
            if (i == 3) dir *= .7f;
            //The timer in the color makes the sunlight switch between yellow light and whitish light
            batch.setColor(.9f, 1, .59f + .3f * Math.abs(MathUtils.sin(desyncedTimer/5)), aa);
            //batch.draw(t, x - tWidth/2 + dir * (i * xScale * tWidth * .04f), y - tHeight/2 * 2f, tWidth/2, 2f * tHeight/2, tWidth, tHeight, xScale, yScale, angleToXDestInDegrees + dir * i * ((0.55f) * Math.abs(MathUtils.sin(GameScreen.ELAPSED_TIME/3))));
            batch.draw(t, x - tWidth/2 + dir * widthScl * (i * xScale * tWidth * .075f), y - tHeight/2 * 2f, tWidth/2, 2f * tHeight/2, tWidth, tHeight, xScale, yScale, angleToXDestInDegrees + dir * i * ((0.55f) * Math.abs(MathUtils.sin(com.gdx.rainbow.screens.GameScreen.ELAPSED_TIME/3))));
            batch.setColor(1, 1, 1, 1);
        }

    }


    public void drawBounds(ShapeRenderer sr) {
        float[] vertices = shape.getVertices();
        sr.setColor(Color.GREEN);
        sr.line(vertices[0], vertices[1], vertices[2], vertices[3]);
        sr.line(vertices[2], vertices[3], vertices[4], vertices[5]);
        sr.line(vertices[4], vertices[5], vertices[6], vertices[7]);
        sr.line(vertices[6], vertices[7], vertices[0], vertices[1]);

        sr.setColor(Color.ORANGE);
        sr.line(xCenterTop, com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2, xDest, -com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2);
        sr.setColor(Color.BROWN);
        sr.line(xCenterTop, com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2, xCenterBottom, -com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2);

        sr.setColor(Color.PINK);
        sr.line(xCenterTop, com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2, nextXDest, -com.gdx.rainbow.screens.GameScreen.UNIT_HEIGHT/2);
    }

    public boolean contains(Body b) {
        return shape.contains(b.getPosition());
    }

}
