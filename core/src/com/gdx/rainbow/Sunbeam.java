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

    private float xStart, xCenter;
    private float width, height;
    public Polygon shape;

    public boolean typeOfMovement = true;

    private float drawLineSpacing = .125f/20;

    private float angleToXDestInDegrees = 0;

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
        float dxx = (xDest - xCenter);

        dxx *= .26f;
        return dxx;
    }

    private void setVertices() {
        float[] vertices = new float[8];
        //First Vertex Top Left
        vertices[0] = xCenter - width/2 + width/8;
        vertices[1] = GameScreen.UNIT_HEIGHT/2;
        //Second Vertex Bottom Left
        vertices[2] = xCenter - width/2 - width/3;
        vertices[3] = GameScreen.UNIT_HEIGHT/2 - height;
        //Third Vertex Bottom Right
        vertices[4] = xCenter + width/2 + width/3;
        vertices[5] = GameScreen.UNIT_HEIGHT/2 - height;
        //Fourth Vertex Top Right
        vertices[6] = xCenter + width/2 - width/8;
        vertices[7] = GameScreen.UNIT_HEIGHT/2;
        shape.setVertices(vertices);
    }

    private void pushVertices(float dx, float delta) {
        float[] vertices = shape.getVertices();

        //Second Vertex Bottom Left xCoord
        vertices[2] += dx * delta;
        xCenter = (vertices[0] + vertices[6]) * .5f;
        //Third Vertex Bottom Right xCoord
        vertices[4] += dx * delta;

        shape.setVertices(vertices);
    }

    private void pushBeam(float dx, float delta) {
        float[] vertices = shape.getVertices();

        for (int i = 0; i < vertices.length; i++) {
            if (i % 2 != 0) continue;
            vertices[i] += dx * delta;
        }

        shape.setVertices(vertices);

        xCenter = (vertices[0] + vertices[6]) * .5f;
    }

    public void update(float xDest, float delta) {
        float dx = calcDx(xDest);
        //dx = 0;
        //if (typeOfMovement) {
            //pushVertices(dx, delta);
        //}
        //else pushBeam(dx, delta);
    pushBeam(dx, delta);

        Vector2 start = new Vector2(xCenter, GameScreen.UNIT_HEIGHT/2);
        start.nor();
        Vector2 end = new Vector2(xCenter + dx * delta, -GameScreen.UNIT_HEIGHT/2);
        end.nor();
        angleToXDestInDegrees = 180 - (180/MathUtils.PI) * (float) Math.acos((start.dot(end)));
        System.out.println(angleToXDestInDegrees);

    }

    public void draw(ShapeRenderer sr) {
        float[] vertices = shape.getVertices();
        sr.setColor(1, 1f, 1f, .6f);

        for (int i = 0; i < width/drawLineSpacing; i ++ ) {
            sr.line(vertices[0] + i * drawLineSpacing, vertices[1], vertices[2] + i * drawLineSpacing, vertices[3]);
        }
        sr.setColor(1, 1, 1, 1);
    }

    public void drawThree(SpriteBatch batch) {
        TextureRegion t = new TextureRegion(Assets.sun_beam_band_image);
        float tWidth = t.getRegionWidth();
        float tHeight = t.getRegionHeight();
        float xScale = .01f;
        float yScale = .08f;
        float x = xCenter;
        float y= GameScreen.UNIT_HEIGHT/2 + .2f;
        float a = .6f;

        batch.setColor(1, 1, .79f, a);

        batch.draw(t, x -tWidth/2, y -tHeight/2 * 2f, tWidth/2, 2f * tHeight/2, tWidth, tHeight, xScale, yScale, angleToXDestInDegrees);

        batch.setColor(1, 1, 1, 1);
    }

    public void drawTwo(SpriteBatch batch) {

        float[] vertices = shape.getVertices();

        TextureRegion t = new TextureRegion(Assets.sun_beam_band_image);
        float tWidth = t.getRegionWidth();
        float tHeight = t.getRegionHeight();
        float xScale = .0005f;
        float yScale = .05f;
        float x = xCenter -tWidth/2;
        float y= -tHeight/2;
        float a = .5f;
        for (int i = 0; i < ((.5f * width) - width/6)/(drawLineSpacing); i ++) {
            //if ((i % 2) != 0) a = 0f;
            float xStart = vertices[0];
            float yStart = vertices[1];

           // float xEnd =
           // float yEnd;

            //Vector2 v = new Vector2(xEnd - xStart, yEnd - yStart);
            //v.set();
            batch.setColor((float)255/255, (float)255/255, (float)178/255, a);
            batch.draw(t, x + (i * drawLineSpacing), y, tWidth / 2, tHeight / 2, tWidth, tHeight, xScale, yScale, 180);
            batch.draw(t, x - ((i + 1) * drawLineSpacing), y, tWidth / 2, tHeight / 2, tWidth, tHeight, xScale, yScale, 180);
        }
        batch.setColor(1, 1, 1, 1);
    }

    public void drawBounds(ShapeRenderer sr) {
        float[] vertices = shape.getVertices();
        sr.setColor(Color.GREEN);
        sr.line(vertices[0], vertices[1], vertices[2], vertices[3]);
        sr.line(vertices[2], vertices[3], vertices[4], vertices[5]);
        sr.line(vertices[4], vertices[5], vertices[6], vertices[7]);
        sr.line(vertices[6], vertices[7], vertices[0], vertices[1]);
    }

    public boolean contains(Body b) {
        return shape.contains(b.getPosition());
    }

}
