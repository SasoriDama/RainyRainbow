package com.gdx.rainbow.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.rainbow.MyGdxGame;

/**
 * Created by WAM on 10/10/2016.
 */
public class LoseScreen extends ScoreScreen {

    public LoseScreen(MyGdxGame game) {
        super(game);
        nextScreen = game.mainMenu;
    }

    @Override
    public void drawExtended(SpriteBatch batch) {
        super.drawExtended(batch);
        game.font.draw(batch, "YOU LOSE SORRY", 0, 250);
    }

}
