package com.gdx.rainbow;


import com.badlogic.gdx.InputProcessor;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Created by WAM on 9/5/2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    OrthographicCamera guiCam;

    World world;

    Cloud c;
    //Box 2D World world

    public GameScreen(MyGdxGame game) {
        this.game = game;

        guiCam = new OrthographicCamera(MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
        world = new World(new Vector2(0, 0), true);
        this.spawnCloud();
    }

    public void spawnCloud() {
        c = new Cloud(world);
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
        game.batcher.draw(Assets.background, -MyGdxGame.WIDTH/2, -MyGdxGame.HEIGHT/2, MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
        game.batcher.draw(Assets.player, c.body.getPosition().x - Assets.player.getWidth()/2, c.body.getPosition().y - Assets.player.getHeight()/2);
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
