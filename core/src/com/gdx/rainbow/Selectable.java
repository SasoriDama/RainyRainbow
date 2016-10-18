package com.gdx.rainbow;

import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
/**
 * Created by WAM on 10/10/2016.
 */
public class Selectable {

    public static final int GAME_MODE = 0, CHARACTER = 1;

    public static ArrayList<Selectable> GAME_MODES = new ArrayList<Selectable>();
    public static ArrayList<Selectable> CHARACTERS = new ArrayList<Selectable>();

    //GAME MODES
    public static Selectable MODE_DEFUALT = new Selectable("Normal", GAME_MODE);
    public static Selectable MODE_CLEAR_SKIES = new Selectable("Clear Skies", GAME_MODE);
    public static Selectable MODE_ZEN = new Selectable("Zen", GAME_MODE);

    public static Selectable CHARACTER_DEFUALT = new Selectable("Murphy", CHARACTER);
    public static Selectable CHARACTER_STORM = new Selectable("Goethe", CHARACTER);

    public String NAME;
    private int TYPE;
    public int ID;
    public Texture IMAGE;

    public static Selectable getNext(int TYPE, int curMode, int i) {

        ArrayList<Selectable> list = null;

        if (TYPE == GAME_MODE) list = GAME_MODES;
        if (TYPE == CHARACTER) list = CHARACTERS;

        int j = curMode + i;
        if (j > list.size() - 1) j = 0;
        if (j < 0) j = list.size() - 1;
        return list.get(j);
    }

    public static void initializeImages() {
        for (Selectable s: GAME_MODES) {
            s.setImage();
        }

        for (Selectable s: CHARACTERS) {
            s.setImage();
        }
    }

    public void setImage() {

        if (TYPE == GAME_MODE) {
            IMAGE = Assets.game_mode_images[ID];
        }

        if (TYPE == CHARACTER) {
            IMAGE = Assets.character_images[ID];
        }

    }

    private Selectable(String name, int TYPE) {
        this.NAME = name;
        this.TYPE = TYPE;
        if (TYPE == Selectable.GAME_MODE) {
            ID = Selectable.GAME_MODES.size();
            Selectable.GAME_MODES.add(this);
        }
        if (TYPE == Selectable.CHARACTER) {
            ID = Selectable.CHARACTERS.size();
            Selectable.CHARACTERS.add(this);
        }
    }

}
