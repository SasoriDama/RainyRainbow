package com.gdx.rainbow.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.rainbow.*;
import com.gdx.rainbow.screens.upgrade.stats.*;
import com.gdx.rainbow.screens.menus.utils.Button;


/**
 * Created by WAM on 9/25/2016.
 */
public class UpgradeScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    OrthographicCamera guiCam;
    Viewport viewport;

    //TEMPORARY
    public static float UNIT_WIDTH = MyGdxGame.WIDTH/2;
    public static float UNIT_HEIGHT = MyGdxGame.HEIGHT/2;

    Vector2 mouseLocation;

    Button[] upgradeButtons;
    Button[] upgradeButtons1;
    Button play;

    public UpgradeScreen(MyGdxGame game) {
        this.game = game;
        //Gdx.input.setInputProcessor(this);
        guiCam = new OrthographicCamera();
        viewport = new FillViewport(UNIT_WIDTH, UNIT_HEIGHT, guiCam);

        viewport.apply();

        guiCam.position.set(0, 0, 0);

        mouseLocation = new Vector2(0, 0);

        //this.stats = stats;

        upgradeButtons = new Button[Stats.STATS.size()];
        upgradeButtons1 = new Button[Stats.STATS.size()];
        play = new Button(0, this.UNIT_HEIGHT/2 - 60, .4f, Assets.stats_screen_plus_button);
    }


    public void update(float delta) {
        //System.out.println("upgrade scren update");
    }

    public void draw(SpriteBatch batch) {

        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);

        batch.begin();
        game.font.draw(batch, "Level: " + game.gameScreen.level, 200, 230);
        game.font.draw(batch, "Total Score: " + game.gameScreen.score.compoundedScore, 200, 190);
        game.font.draw(batch, "Points:  " + game.gameScreen.points, 200, 160);
        for (int i = 0; i < com.gdx.rainbow.screens.upgrade.stats.Stats.STATS.size(); i++) {
            Stat stat = Stats.STATS.get(i);
            float x = -150;
            float y = 160 - i * 50;
            game.font.draw(batch, stat.name + ": ", x- 140, y);

            for (int j = 0; j < game.gameScreen.stats.upgradedAmt[i]; j++) {
                game.font.draw(batch, "x", x + 10 + 20 * j, y);
            }

            for (int h = 0; h < com.gdx.rainbow.screens.upgrade.stats.Stats.NUM_OF_UPGRADES - game.gameScreen.stats.upgradedAmt[i]; h++) {
                game.font.draw(batch, ".", x + 10 + 20 * game.gameScreen.stats.upgradedAmt[i] + 20 * h, y);
            }

        }

        for (int d = 0; d < upgradeButtons.length; d++) {
            float scl = .3f;
            float x = 100;
            float y = 160 - d * 50;
            upgradeButtons[d] = new Button(x, y, scl, Assets.stats_screen_plus_button);
            upgradeButtons[d].draw(batch, game.font);
        }

        for (int d = 0; d < upgradeButtons.length; d++) {
            float scl = .3f;
            float x = 100 + 50;
            float y = 160 - d * 50;
            upgradeButtons1[d] = new Button(x, y, scl, Assets.stats_screen_minus_button);
            upgradeButtons1[d].draw(batch, game.font);
        }

        play.draw(batch, game.font);

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

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float pixX = (float) (screenX - MyGdxGame.WIDTH/2);
        float pixY = (float) (-(screenY - MyGdxGame.HEIGHT/2));
        mouseLocation.set(pixX/2, pixY/2);

        //System.out.println(mouseLocation);

        for (int d = 0; d < upgradeButtons.length; d++) {
            Button b = (upgradeButtons[d]);
            if (b.containsPoint(mouseLocation.x, mouseLocation.y)) {
                if (game.gameScreen.points > 0) if (game.gameScreen.stats.upgradeStat(Stats.STATS.get(d), 1)) game.gameScreen.points -= 1;
            }
            b = (upgradeButtons1[d]);
            if (b.containsPoint(mouseLocation.x, mouseLocation.y)) {
                if (game.gameScreen.stats.upgradeStat(Stats.STATS.get(d), -1)) game.gameScreen.points += 1;
            }
        }

        if (play.containsPoint(mouseLocation.x, mouseLocation.y)) {
            game.set(game.gameScreen);
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
