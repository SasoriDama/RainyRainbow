package com.gdx.rainbow.screens.upgrade.stats;

/**
 * Created by WAM on 9/25/2016.
 */
public class Stat {

    public String name;
    public float startVal;
    public float endVal;

    public Stat(String name, float startVal, float endVal) {
        this.name = name;
        this.startVal = startVal;
        this.endVal = endVal;
        Stats.STATS.add(this);
    }
}
