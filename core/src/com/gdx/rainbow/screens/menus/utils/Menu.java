package com.gdx.rainbow.screens.menus.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.rainbow.*;
import com.gdx.rainbow.screens.upgrade.stats.*;

import java.util.ArrayList;


/**
 * Created by WAM on 9/25/2016.
 */
public abstract class Menu extends ScreenAdapter implements InputProcessor {

    protected MyGdxGame game;

    //TEMPORARY
    public static float UNIT_WIDTH = MyGdxGame.WIDTH/2;
    public static float UNIT_HEIGHT = MyGdxGame.HEIGHT/2;

    Vector2 mouseLocation;

    protected Texture bg;
    protected ArrayList<Button> buttons;

    public Menu(MyGdxGame game) {
        this.game = game;
        game.guiCam = new OrthographicCamera();
        game.viewport = new FillViewport(UNIT_WIDTH, UNIT_HEIGHT, game.guiCam);
        game.font = new BitmapFont();

        game.viewport.apply();

        game.guiCam.position.set(0, 0, 0);

        mouseLocation = new Vector2(0, 0);
        buttons = new ArrayList<Button>();

    }

    public abstract void onScreenSwitch(ScreenAdapter previousScreen);

    protected void drawBG(SpriteBatch batch) {
        if (bg != null) {
            TextureRegion r = new TextureRegion(bg);
            float scaleX = Gdx.graphics.getWidth()/2;
            float scaleY = Gdx.graphics.getHeight()/2;
            batch.draw(r, -scaleX/2, -scaleY/2, scaleX, scaleY);
        }
    }

    protected void drawExtended(SpriteBatch batch) {

    }

    public void update(float delta) {
    }

    public void draw(SpriteBatch batch) {

        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.guiCam.update();
        batch.setProjectionMatrix(game.guiCam.combined);

        batch.begin();

        drawBG(game.batcher);

        for (Button b: buttons) {
            b.draw(game.batcher, game.font);
        }

        drawExtended(game.batcher);

        batch.end();
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
        float pixX = (float) (screenX - MyGdxGame.WIDTH/2);
        float pixY = (float) (-(screenY - MyGdxGame.HEIGHT/2));
        mouseLocation.set(pixX/2, pixY/2);

        //System.out.println(mouseLocation);

        for (Button b: buttons) {
            if (b.containsPoint(mouseLocation.x, mouseLocation.y)) {
                b.pressed = true;
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float pixX = (float) (screenX - MyGdxGame.WIDTH/2);
        float pixY = (float) (-(screenY - MyGdxGame.HEIGHT/2));
        mouseLocation.set(pixX/2, pixY/2);

        //System.out.println(mouseLocation);

        for (Button b: buttons) {
                b.pressed = false;
            }

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
