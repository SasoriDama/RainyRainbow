package com.gdx.rainbow.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.MyGdxGame;
import com.gdx.rainbow.Selectable;
import com.gdx.rainbow.screens.menus.utils.*;

/**
 * Created by WAM on 10/10/2016.
 */
public class MainMenu extends Menu {

    Button start;
    Button addToMode, subtractFromMode;
    Button quit;

    private Selectable mode = Selectable.MODE_DEFUALT;

    private String modeText = "";

    public MainMenu(MyGdxGame game) {
        super(game);

        bg = Assets.main_menu_background_image;

        start = new Button(0, -20, .15f, Assets.button_up, "Play!");
        //start.clickedImage = Assets.button_down;

        addToMode = new Button(100, -20 - 75, .5f, Assets.stats_screen_plus_button, "Next");
        subtractFromMode = new Button(-110, -20 - 75, .5f, Assets.stats_screen_minus_button, "Previous");

        quit = new Button(0, -200, .15f, Assets.button_up, "Quit");

        buttons.add(start);

        buttons.add(addToMode);
        buttons.add(subtractFromMode);

        buttons.add(quit);
    }

    @Override
    public void onScreenSwitch(ScreenAdapter previousScreen) {
        game.gameScreen.resetPlayThrough();
    }

    @Override
    public void drawExtended(SpriteBatch batch) {
        batch.draw(mode.IMAGE, -50, -50 - 110, 100, 100);
        game.font.draw(batch, modeText, -10, -140);
    }

    public void update(float delta) {

        if (addToMode.isPressed()) {
            mode  = Selectable.getNext(Selectable.GAME_MODE, mode.ID, 1);
        }
        if (subtractFromMode.isPressed()) {
            mode = Selectable.getNext(Selectable.GAME_MODE, mode.ID, -1);
        }

        modeText = mode.NAME;

        if (MyGdxGame.UNLOCKS.unlockedGameModes.contains(mode)) {
            if (start.isPressed()) {
                game.gameScreen.MODE = mode;
                game.set(game.characterSelectScreen);
            }
        }
        else {
            modeText += "\n LOCKED";
        }

        if (quit.isPressed()) {
            Gdx.app.exit();
        }

    }

}
