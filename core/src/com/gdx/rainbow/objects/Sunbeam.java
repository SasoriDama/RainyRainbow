package com.gdx.rainbow.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gdx.rainbow.MyGdxGame;

/**
 * Created by WAM on 9/5/2016.
 */
public class Sunbeam {

    private float xStart, xCenter;
    private float width, height;
    public Polygon shape;

    private float drawLineSpacing = 3f;

    public Sunbeam() {
        shape = new Polygon();
        width = 200;
        height = MyGdxGame.HEIGHT;
        xStart = -width/2;

        setVerteces();
    }

    private float calcDx(float xDest) {
        //x comp of vector between center and newX
        float dxx = ((xDest) - xCenter);

        float dx = 1;
        if (dxx < 0) dx = -1;
        if (dxx > 0) dx = 1;
        dx *= 10;
        return dxx;
    }

    private void setVerteces() {
        float[] vertices = new float[8];
        //First Vertex Top Left
        vertices[0] = xStart;
        vertices[1] = MyGdxGame.HEIGHT/2;
        //Second Vertex Bottom Left
        vertices[2] = xStart;
        vertices[3] = MyGdxGame.HEIGHT/2 - height + 1;
        //Third Vertex Bottom Right
        vertices[4] = xStart + width;
        vertices[5] = MyGdxGame.HEIGHT/2 - height + 1;
        //Fourth Vertex Top Right
        vertices[6] = xStart + width;
        vertices[7] = MyGdxGame.HEIGHT/2;
        shape.setVertices(vertices);
    }

    private void setVerteces(float dx) {
        float[] vertices = new float[8];
        //First Vertex Top Left
        vertices[0] = xStart;
        vertices[1] = MyGdxGame.HEIGHT/2;
        //Second Vertex Bottom Left
        vertices[2] = xStart + dx;
        vertices[3] = MyGdxGame.HEIGHT/2 - height + 1;
        //Third Vertex Bottom Right
        vertices[4] = xStart + width + dx;
        vertices[5] = MyGdxGame.HEIGHT/2 - height + 1;
        //Fourth Vertex Top Right
        vertices[6] = xStart + width;
        vertices[7] = MyGdxGame.HEIGHT/2;
        shape.setVertices(vertices);
    }

    private void pushVerteces(float dx, float delta) {
        float[] vertices = shape.getVertices();

        //Second Vertex Bottom Left xCoord
        vertices[2] += dx * delta;
        //Third Vertex Bottom Right xCoord
        vertices[4] += dx * delta;

        shape.setVertices(vertices);
    }

    public void update(float xDest, float delta) {
        //setVerteces(xDest);
        xCenter = xStart + width/2;
        float dx = calcDx(xDest);
        //System.out.println(dx);
        //if (xCenter + dx * delta != xDest)
        pushVerteces(dx, delta);
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
        sr.setColor(Color.WHITE);

        for (int i = 0; i < width/drawLineSpacing; i ++ ) {
            sr.line(MyGdxGame.WIDTH/2 + vertices[0] + i * drawLineSpacing, MyGdxGame.HEIGHT/2 + vertices[1], MyGdxGame.WIDTH/2 + vertices[2] + i * drawLineSpacing, MyGdxGame.HEIGHT/2 + vertices[3]);
        }
    }

    public void drawBounds(ShapeRenderer sr) {
        float[] vertices = shape.getVertices();
        sr.setColor(Color.BLACK);
        sr.line(MyGdxGame.WIDTH/2 + vertices[0], MyGdxGame.HEIGHT/2 + vertices[1], MyGdxGame.WIDTH/2 + vertices[2], MyGdxGame.HEIGHT/2 + vertices[3]);
        sr.line(MyGdxGame.WIDTH/2 + vertices[2], MyGdxGame.HEIGHT/2 + vertices[3], MyGdxGame.WIDTH/2 + vertices[4], MyGdxGame.HEIGHT/2 + vertices[5]);
        sr.line(MyGdxGame.WIDTH/2 + vertices[4], MyGdxGame.HEIGHT/2 + vertices[5], MyGdxGame.WIDTH/2 + vertices[6], MyGdxGame.HEIGHT/2 + vertices[7]);
        sr.line(MyGdxGame.WIDTH/2 + vertices[6], MyGdxGame.HEIGHT/2 + vertices[7], MyGdxGame.WIDTH/2 + vertices[0], MyGdxGame.HEIGHT/2 + vertices[1]);
    }

    public boolean contains(Body b) {
        return shape.contains(b.getPosition());
    }

}
