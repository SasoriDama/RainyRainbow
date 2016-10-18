package com.gdx.rainbow.screens;

import com.gdx.rainbow.MyGdxGame;

/**
 * Created by WAM on 10/10/2016.
 */
public class Score {

    public static int HIGH_SCORE = 0;
    public int compoundedScore = 0;
    public float percentOfTimeLeft;
    public boolean wonWithRareCloud = false;

    public int scoreFromTimeLeft, scoreFromRareCloud;

    public void reset() {
        compoundedScore = 0;
        wonWithRareCloud = false;
    }

    public int generateScore() {

        scoreFromTimeLeft = (int) (1000 * (percentOfTimeLeft));
        scoreFromRareCloud = 0;
        if (wonWithRareCloud) scoreFromRareCloud = 1000;

        int score = scoreFromTimeLeft + scoreFromRareCloud;

        if (HIGH_SCORE < compoundedScore) {
            HIGH_SCORE = compoundedScore;
            MyGdxGame.PREFS.putInteger("High_Score", HIGH_SCORE);
        }


        return score;
    }

}
