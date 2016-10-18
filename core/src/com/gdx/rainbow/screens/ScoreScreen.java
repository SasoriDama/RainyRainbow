package com.gdx.rainbow.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.MyGdxGame;
import com.gdx.rainbow.screens.menus.utils.*;
import com.gdx.rainbow.screens.upgrade.stats.Stat;

/**
 * Created by WAM on 10/10/2016.
 */
public class ScoreScreen extends Menu {

    private Button cont;

    protected ScreenAdapter nextScreen = game.upgradeScreen;

    public ScoreScreen(MyGdxGame game) {
        super(game);
        cont = new Button(0, -200, .5f, Assets.stats_screen_plus_button, "Continue");
        buttons.add(cont);
    }

    @Override
    public void onScreenSwitch(ScreenAdapter previousScreen) {

    }

    @Override
    public void update(float delta) {
        if (cont.isPressed()) {
            game.set(nextScreen);
        }
    }

    @Override
    public void drawExtended(SpriteBatch batch) {
        if (game.gameScreen.score.generateScore() > 0) game.font.draw(batch, "Score: " + game.gameScreen.score.generateScore(), 0, 0);
        game.font.draw(batch, "Level: " + (game.gameScreen.level), 0, 200);
        if (game.gameScreen.score.generateScore() > 0) game.font.draw(batch, "Score BreakDown: ", 200, 20);
        if (game.gameScreen.score.scoreFromTimeLeft != 0) game.font.draw(batch, "From Time Left: " + (game.gameScreen.score.scoreFromTimeLeft), 200, 0);
        if (game.gameScreen.score.scoreFromRareCloud != 0) game.font.draw(batch, "From Rare Cloud: " + (game.gameScreen.score.scoreFromRareCloud), 200, -30);
        game.font.draw(batch, "Total Score: " + game.gameScreen.score.compoundedScore, 0, -100);
        game.font.draw(batch, "High Score " + Score.HIGH_SCORE, 0, -50);
    }

}
