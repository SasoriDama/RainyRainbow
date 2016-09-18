package com.gdx.rainbow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
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

    public Sunbeam() {
        shape = new Polygon();
        //width = 200;
        width = 4f * (Player.SIZE);
        height = GameScreen.UNIT_HEIGHT;
        xStart = -width/2;
        System.out.println("SUNBEAM MOVEMENT DISABLED");
        setVerteces();
    }

    private float calcDx(float xDest) {
        //x comp of vector between center and newX
        float dxx = (xDest - xCenter);
        //if (dxx < .001) return 0;

        float dx = 1;
        if (dxx < 0) dx = -1;
        if (dxx > 0) dx = 1;
        dxx *= .26f;
        return dxx;
    }

    private void setVerteces() {
        float[] vertices = new float[8];
        //First Vertex Top Left
        vertices[0] = xStart;
        vertices[1] = GameScreen.UNIT_HEIGHT/2;
        //Second Vertex Bottom Left
        vertices[2] = xStart;
        vertices[3] = GameScreen.UNIT_HEIGHT/2 - height;
        //Third Vertex Bottom Right
        vertices[4] = xStart + width;
        vertices[5] = GameScreen.UNIT_HEIGHT/2 - height;
        //Fourth Vertex Top Right
        vertices[6] = xStart + width;
        vertices[7] = GameScreen.UNIT_HEIGHT/2;
        shape.setVertices(vertices);
    }

    private void pushVerteces(float dx, float delta) {
        float[] vertices = shape.getVertices();

        //Second Vertex Bottom Left xCoord
        vertices[2] += dx * delta;
        xCenter = vertices[2] + width/2;
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

        xCenter = vertices[2] + width/2;
    }

    public void update(float xDest, float delta) {
        //setVerteces(xDest);
        //xCenter = shape.getVertices().get
        float dx = calcDx(xDest);
        dx = 0;
        //if (xCenter + dx * delta != xDest)
        if (typeOfMovement) pushVerteces(dx, delta);
        else pushBeam(dx, delta);
         /* float[] vertices = shape.getVertices();

        float dx = 0;
        if (xDest - xCenter < 0) dx = -1;
        if (xDest - xCenter > 0) dx = 1;
        vertices[2] += (dx) * 100 * delta;
        //Third Vertex Bottom Right xCoord
        vertices[4] +=  (dx) * 100 * delta;

        shape.setVertices(vertices);
        */
    }

    public void draw(ShapeRenderer sr) {
        float[] vertices = shape.getVertices();
        sr.setColor(1, 1, 1, .6f);

        for (int i = 0; i < width/drawLineSpacing; i ++ ) {
            sr.line(vertices[0] + i * drawLineSpacing, vertices[1], vertices[2] + i * drawLineSpacing, vertices[3]);
        }
        sr.setColor(1, 1, 1, 1);
    }

    public void drawBounds(ShapeRenderer sr) {
        float[] vertices = shape.getVertices();
        sr.setColor(Color.BLACK);
        sr.line(vertices[0], vertices[1], vertices[2], vertices[3]);
        sr.line(vertices[2], vertices[3], vertices[4], vertices[5]);
        sr.line(vertices[4], vertices[5], vertices[6], vertices[7]);
        sr.line(vertices[6], vertices[7], vertices[0], vertices[1]);
    }

    public boolean contains(Body b) {
        return shape.contains(b.getPosition());
    }

}
