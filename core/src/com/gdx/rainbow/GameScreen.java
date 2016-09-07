package com.gdx.rainbow;


import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gdx.rainbow.objects.Object;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import sun.security.provider.Sun;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by WAM on 9/5/2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    OrthographicCamera guiCam;

    World world;
    ArrayList<Object> objects;
    Player player;
    Sunbeam sunBeam;

    float nextBeamLocation = 0;
    float beamTimer = 0;
    float nextBeamTime = 10;

    float cloudTimer = 0;
    float nextCloudTime = 4;

    //Temporary Shape Renderer
    ShapeRenderer sr;

    public GameScreen(MyGdxGame game) {
        this.game = game;
        Gdx.input.setInputProcessor(this);
        guiCam = new OrthographicCamera(MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
        world = new World(new Vector2(0, 0), true);

        objects = new ArrayList<Object>();

        player = (Player) Object.createObject(Object.PLAYER);
        player.set(world, 0, 0);

        sunBeam = new Sunbeam();

        sr = new ShapeRenderer();
    }

    public void spawnCloud() {

        float x = 0;
        float y = 0;

        Vector2 initialVel = new Vector2(0, 0);

        boolean left = MathUtils.randomBoolean();

        if (left) {
            x = -MyGdxGame.WIDTH/2 - 100;
            initialVel.x = 60;
        }
        if (!left) {
            x = MyGdxGame.WIDTH/2 - 100;
            initialVel.x = -60;
        }

        y = MathUtils.random(MyGdxGame.HEIGHT);

        Cloud c = (Cloud) Object.createObject(Object.CLOUD);
        c.set(world, x, y, initialVel);
    }


    public void update(float delta) {
        world.step(delta, 6, 2);
        this.handleSunbeam(delta);
        this.handleClouds(delta);
    }

    private void handleSunbeam(float delta) {
        this.beamTimer += delta;
        //System.out.println(nextBeamLocation);
        if (beamTimer >= nextBeamTime) {
            beamTimer = 0;
            //nextBeamLocation = MathUtils.random(-MyGdxGame.WIDTH/2, MyGdxGame.WIDTH/2);
            //nextBeamLocation = MyGdxGame.WIDTH/2;
        }
        sunBeam.update(nextBeamLocation, delta);
    }

    private void handleClouds(float delta) {
        this.cloudTimer += delta;
        if (cloudTimer >= nextCloudTime) {
            cloudTimer = 0;
            this.spawnCloud();
        }
    }

    private void applyDrag() {
        for (Object o: objects) {
            //o.body.applyForce();
        }
    }

    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        game.batcher.begin();
        game.batcher.draw(Assets.background_image, -MyGdxGame.WIDTH/2, -MyGdxGame.HEIGHT/2, MyGdxGame.WIDTH, MyGdxGame.HEIGHT);

        Box2DSprite.draw(game.batcher, world);

        game.batcher.end();


        sr.begin(ShapeRenderer.ShapeType.Line);
        sunBeam.draw(sr);
        sr.end();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void dispose() {
        world.dispose();
        sr.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        this.nextBeamLocation = screenX - MyGdxGame.WIDTH/2;
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
