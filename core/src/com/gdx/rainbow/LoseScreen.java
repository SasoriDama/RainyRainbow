package com.gdx.rainbow;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by WAM on 10/7/2016.
 */
public class LoseScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    public LoseScreen(MyGdxGame game) {
        this.game = game;
    }

    public void update(float delta) {

    }

    public void draw(SpriteBatch batch) {

    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(game.batcher);
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

