package com.gdx.rainbow.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.MyGdxGame;
import com.gdx.rainbow.screens.menus.utils.*;

/**
 * Created by WAM on 10/11/2016.
 */
public class HelpScreen extends Menu {

    Button back;
    Button mainMenu;

    public HelpScreen(MyGdxGame game) {
        super(game);
        bg = Assets.help_screen_background;

        back = new Button(-Menu.UNIT_WIDTH/2 + 100, -100, .15f, Assets.button_up, "Back");
        mainMenu = new Button(Menu.UNIT_WIDTH/2 - 100, -100, .15f, Assets.button_up, "Main Menu \n (Lose Progress)");
        buttons.add(back);
        buttons.add(mainMenu);
    }

    @Override
    public void onScreenSwitch(ScreenAdapter previousScreen) {

    }


    @Override
    public void update(float delta) {
        super.update(delta);

        if (back.isPressed()) {
            game.set(game.gameScreen);
        }

        if (mainMenu.isPressed()) {
            game.set(game.mainMenu);
        }

    }

}
