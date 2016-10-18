package com.gdx.rainbow.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.rainbow.MyGdxGame;

/**
 * Created by WAM on 10/10/2016.
 */
public class WinScreen extends ScoreScreen {

    public int pointsAdded;

    public WinScreen(MyGdxGame game) {
        super(game);
    }

    @Override
    public void drawExtended(SpriteBatch batch) {
        super.drawExtended(batch);
        game.font.draw(batch, "Congrats!", 0, 250);
        if (game.gameScreen.score.wonWithRareCloud) game.font.draw(batch, "RARE CLOUD WIN!!", 0, 150);
        game.font.draw(batch, "Points Added: " + pointsAdded, 0, 100);
    }

}
