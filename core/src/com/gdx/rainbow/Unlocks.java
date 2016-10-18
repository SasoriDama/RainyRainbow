package com.gdx.rainbow;

import java.util.ArrayList;
import com.badlogic.gdx.Preferences;

/**
 * Created by WAM on 10/15/2016.
 */
public class Unlocks {

    public ArrayList<Selectable> unlockedGameModes;
    public ArrayList<Selectable> unlockedCharacters;

    public Unlocks(Preferences prefs) {
        unlockedGameModes = new ArrayList<Selectable>();
        unlockedCharacters = new ArrayList<Selectable>();

        unlockedGameModes.add(Selectable.MODE_DEFUALT);
        unlockedCharacters.add(Selectable.CHARACTER_DEFUALT);

        if (prefs.getBoolean("Unlocked_Mode_ClearSkies")) {
            unlockedGameModes.add(Selectable.MODE_CLEAR_SKIES);
        }
        if (prefs.getBoolean("Unlocked_Mode_Zen")) {
            unlockedGameModes.add(Selectable.MODE_ZEN);
        }

        if (prefs.getBoolean("Unlocked_Character_Storm")) {
            unlockedCharacters.add(Selectable.CHARACTER_STORM);
        }

    }

}
