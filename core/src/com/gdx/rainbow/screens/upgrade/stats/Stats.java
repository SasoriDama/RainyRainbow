package com.gdx.rainbow.screens.upgrade.stats;

import java.util.ArrayList;

/**
 * Created by WAM on 9/25/2016.
 */
public class Stats {

    public static final ArrayList<Stat> STATS = new ArrayList<Stat>();

    //Player STATS start and end values for upgrades
    public static final Stat SPEED = new Stat("Speed", 1.5f, 4.5f);
    public static final Stat ACCELERATION = new Stat("Acceleration", .8f, 5f);    //How easily the player accelerates and deccelerates. This is done by changing the players density to be lighter
    public static final Stat PUSH_RANGE = new Stat("Blow Range", 1f, 2.75f);
    public static final Stat PUSH_STRENGTH = new Stat("Blow Strength", 2f, 7f);//3);//1.75f);
    public static final Stat PUSHBACK_FORCE = new Stat("Blowback", 3.5f, 0f);
    public static final Stat SUN_RESET = new Stat("Sun Timer Reset", 10, .6f);
    public static final Stat WIND_RESIST = new Stat("P Wind Resist", 5f, 0f);
    public static final Stat SUN_CLOUD_SIZE_MOD = new Stat("Cloud Sun Hogging", 1f, 0f);


    public static final Stats END_STATS = new Stats(1, 1, 1, 1, 1, 1, 1, 1);

    public static final int NUM_OF_UPGRADES = 10;

    //instance values
    public float speed;
    public float acceleration;
    public float pushRange;
    public float pushStrength;
    public float pushBackForce;
    public float sunReset;
    public float windResist;
    public float sunCloudSizeMod;

    public float[] stats = new float[STATS.size()];
    public int[] upgradedAmt = new int[STATS.size()];

    public Stats () {
       this.set(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public Stats(float percentSpeed, float percentAcceleration, float percentPushRange, float percentPushStrength, float percentPushBackForce, float percentSunReset, float percentWindResist, float percentSunCloudSizeMod) {
       this.set(percentSpeed, percentAcceleration, percentPushRange, percentPushStrength, percentPushBackForce, percentSunReset, percentWindResist, percentSunCloudSizeMod);
        for (int i = 0; i < upgradedAmt.length; i++) {
            upgradedAmt[i] = 0;
        }
    }

    public void set(float percentSpeed, float percentAcceleration, float percentPushRange, float percentPushStrength, float percentPushBackForce, float percentSunReset, float percentWindResist, float percentSunCloudSizeMod) {
        this.speed = lerp(SPEED.startVal, SPEED.endVal, percentSpeed);
        this.acceleration = lerp(ACCELERATION.startVal, ACCELERATION.endVal, percentAcceleration);
        this.pushRange = lerp(PUSH_RANGE.startVal, PUSH_RANGE.endVal, percentPushRange);
        this.pushStrength = lerp(PUSH_STRENGTH.startVal, PUSH_STRENGTH.endVal, percentPushStrength);
        this.pushBackForce = lerp(PUSHBACK_FORCE.startVal, PUSHBACK_FORCE.endVal, percentPushBackForce);
        this.sunReset = lerp(SUN_RESET.startVal, SUN_RESET.endVal, percentSunReset);
        this.windResist = lerp(WIND_RESIST.startVal, WIND_RESIST.endVal, percentWindResist);
        this.sunCloudSizeMod = lerp(SUN_CLOUD_SIZE_MOD.startVal, SUN_CLOUD_SIZE_MOD.endVal, percentSunCloudSizeMod);

        stats[0] = speed;
        stats[1] = acceleration;
        stats[2] = pushRange;
        stats[3] = pushStrength;
        stats[4] = pushBackForce;
        stats[5] = sunReset;
        stats[6] = windResist;
        stats[7] = sunCloudSizeMod;
    }

    public boolean upgradeStat(Stat stat, int amt) {
        int index = 0;
        for (int i = 0; i < STATS.size(); i++) {
            if (stat == STATS.get(i)) index = i;
        }

        this.upgradedAmt[index] += amt;
        if (this.upgradedAmt[index] > Stats.NUM_OF_UPGRADES) {
            this.upgradedAmt[index] = Stats.NUM_OF_UPGRADES;
            return false;
        }
        if (this.upgradedAmt[index] < 0) {
            this.upgradedAmt[index] = 0;
            return false;
        }
        float percent = (float)this.upgradedAmt[index]/Stats.NUM_OF_UPGRADES;
        this.stats[index] = lerp(stat.startVal, stat.endVal, percent);

        speed = stats[0];
        acceleration = stats[1];
        pushRange =  stats[2];
        pushStrength = stats[3];
        pushBackForce = stats[4];
        sunReset = stats[5];
        windResist = stats[6];
        sunCloudSizeMod = stats[7];

        return true;
    }

    private float lerp(float start, float end, float percent) {
        return (start + (percent * (end - start)));
    }

}
