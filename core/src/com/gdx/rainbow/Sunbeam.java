package com.gdx.rainbow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gdx.rainbow.objects.Player;
import com.gdx.rainbow.MyGdxGame;

/**
 * Created by WAM on 9/5/2016.
 */
public class Sunbeam {

    private float xStart, xCenterTop, xCenterBottom;
    private float width, height;
    public Polygon shape;

    public boolean typeOfMovement = true;

    //private float drawLineSpacing = .125f/20;

    private float angleToXDestInDegrees = 0;

    float xDestt;

    public Sunbeam() {
        shape = new Polygon();
        width = 4f * (.2f);
        height = GameScreen.UNIT_HEIGHT;
        xStart = -width/2;
        //System.out.println("SUNBEAM MOVEMENT DISABLED");
        setVertices();
    }

    private float calcDx(float xDest) {
        //x comp of vector between center and newX
        float dxx = (xDest - xCenterBottom);

        dxx *= .26f;
        return dxx;
    }

    public boolean readyForNextLocation() {
        return (Math.abs(xCenterBottom - xDestt) < 1f);
    }

    private void setVertices() {
        float[] vertices = new float[8];
        //First Vertex Top Left
        vertices[0] = xCenterTop - width/2 + width/8;
        vertices[1] = GameScreen.UNIT_HEIGHT/2;
        //Second Vertex Bottom Left
        vertices[2] = xCenterTop - width/2 - width/3;
        vertices[3] = GameScreen.UNIT_HEIGHT/2 - height;
        //Third Vertex Bottom Right
        vertices[4] = xCenterTop + width/2 + width/3;
        vertices[5] = GameScreen.UNIT_HEIGHT/2 - height;
        //Fourth Vertex Top Right
        vertices[6] = xCenterTop + width/2 - width/8;
        vertices[7] = GameScreen.UNIT_HEIGHT/2;
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
        this.xDestt = xDest;
        //dx = 0;
        if (typeOfMovement) {
            pushVertices(dx, delta);

            //Only recalculate angle if the beam is doing rotational movement
            Vector2 start = new Vector2(xCenterBottom - xCenterTop, GameScreen.UNIT_HEIGHT / 2 - -GameScreen.UNIT_HEIGHT / 2);
            start.nor();
            Vector2 end = new Vector2(xCenterBottom + dx * delta - xCenterTop, GameScreen.UNIT_HEIGHT / 2 - -GameScreen.UNIT_HEIGHT / 2);
            end.nor();
            float dTheta = (180 / MathUtils.PI) * (float) Math.acos((start.dot(end)));
            float dir = 1;
            if (xCenterBottom - xDest > 0) dir = -1;
            if (dTheta > .01f) angleToXDestInDegrees += dir * dTheta;
            //System.out.println(angleToXDestInDegrees);
        } else pushBeam(dx, delta);

    }

    public void draw(SpriteBatch batch) {

        TextureRegion t = new TextureRegion(Assets.sun_beam_band_image);
        float tWidth = t.getRegionWidth();
        float tHeight = t.getRegionHeight();
        float xScale = .01f;
        float yScale = .08f;
        float x = xCenterTop;
        float y= GameScreen.UNIT_HEIGHT/2 + .2f;
        float a = .25f + .23f * MathUtils.sin((GameScreen.ELAPSED_TIME + MathUtils.PI)/3) + .05f * (MathUtils.sin(GameScreen.ELAPSED_TIME));
        if (a < 0) a = 0;

        batch.setColor(1, 1, .95f, a);
        batch.draw(t, x -tWidth/2, y -tHeight/2 * 2f, tWidth/2, 2f * tHeight/2, tWidth, tHeight, xScale, yScale, angleToXDestInDegrees);
        batch.setColor(1, 1, 1, 1);

        float timerDesyncValues[] = {5, 3, 7, 1};
        int numOfExtraBeams = 4;

        //add more glimmer
        for (int i = 0; i < numOfExtraBeams; i++) {
            float aa = a - .45f + (.2f * MathUtils.sin((GameScreen.ELAPSED_TIME + timerDesyncValues[i])/2)) + (.175f * MathUtils.sin((GameScreen.ELAPSED_TIME + timerDesyncValues[i]) * 3));
            if (aa < .2f) aa = .2f;

            float dir = 1;
            if (i % 2 != 0) dir = -1;

            batch.setColor(.9f, 1, .59f, aa);
            batch.draw(t, x -tWidth/2 + dir * i * (.14f + (.05f * MathUtils.sin((GameScreen.ELAPSED_TIME + timerDesyncValues[i])/2))), y -tHeight/2 * 2f, tWidth/2, 2f * tHeight/2, tWidth, tHeight, xScale * .65f, yScale, angleToXDestInDegrees + dir * i * (1.0f));
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
        sr.line(xCenterTop, GameScreen.UNIT_HEIGHT/2, xDestt, -GameScreen.UNIT_HEIGHT/2);
        sr.setColor(Color.BROWN);
        sr.line(xCenterTop, GameScreen.UNIT_HEIGHT/2, xCenterBottom, -GameScreen.UNIT_HEIGHT/2);
    }

    public boolean contains(Body b) {
        return shape.contains(b.getPosition());
    }

}
