package com.gdx.rainbow;


import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gdx.rainbow.objects.Object;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

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

    public GameScreen(MyGdxGame game) {
        this.game = game;

        guiCam = new OrthographicCamera(MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
        world = new World(new Vector2(0, 0), true);

        objects = new ArrayList<Object>();

        this.spawnCloud();
        player = (Player) Object.createObject(Object.PLAYER);
        player.set(world, 0, 0);
    }

    public void spawnCloud() {

        float x = 0;
        float y = 0;

        Vector2 initialVel = new Vector2(0, 0);

        boolean left = MathUtils.randomBoolean();

        if (left) {
            x = 100;
            initialVel.x = 60;
        }
        if (!left) {
            x = MyGdxGame.WIDTH - 100;
            initialVel.x = -60;
        }

        //y = MathUtils.random(MyGdxGame.HEIGHT);
        y = 40;
        x = 0;
        y = 0;

        Cloud c = (Cloud) Object.createObject(Object.CLOUD);
        c.set(world, x, y, initialVel);
    }


    public void update(float delta) {
        world.step(delta, 6, 2);
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

    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void dispose() {
        world.dispose();
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
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
