package com.gdx.rainbow.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.MyGdxGame;
import com.gdx.rainbow.Selectable;
import com.gdx.rainbow.Unlocks;
import com.gdx.rainbow.screens.menus.utils.*;

/**
 * Created by WAM on 10/10/2016.
 */
public class CharacterSelectScreen extends Menu {

    Button start;
    Button add, subtract;
    Button back;

    private Selectable character = Selectable.CHARACTER_DEFUALT;

    private String text = "";

    public CharacterSelectScreen (MyGdxGame game) {
        super(game);

        start = new Button(0, -20, .15f, Assets.button_up, "Go");
        //start.clickedImage = Assets.button_down;

        add = new Button(100, -20 - 75, .5f, Assets.stats_screen_plus_button, "Next");
        subtract = new Button(-110, -20 - 75, .5f, Assets.stats_screen_minus_button, "Previous");

        back = new Button(0, -200, .15f, Assets.button_up, "Back");

        buttons.add(start);

        buttons.add(add);
        buttons.add(subtract);

        buttons.add(back);
    }

    @Override
    public void onScreenSwitch(ScreenAdapter previousScreen) {

    }

    @Override
    public void drawExtended(SpriteBatch batch) {
        batch.draw(character.IMAGE, -50, -50 - 110, 100, 100);
        game.font.draw(batch, text, -10, -140);
    }

    public void update(float delta) {

        if (add.isPressed()) {
            character = Selectable.getNext(Selectable.CHARACTER,character.ID, 1);
        }
        if (subtract.isPressed()) {
            character = Selectable.getNext(Selectable.CHARACTER,character.ID, -1);
        }

        text = character.NAME;

        if (MyGdxGame.UNLOCKS.unlockedCharacters.contains(character)) {
            if (start.isPressed()) {
                game.gameScreen.SELECTED_CHARACTER = character;
                game.set(game.gameScreen);
            }
        }
        else {
            text += "\n LOCKED";
        }

        if (back.isPressed()) {
            game.set(game.mainMenu);
        }

    }

}
