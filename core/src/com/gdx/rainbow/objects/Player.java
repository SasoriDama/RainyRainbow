package com.gdx.rainbow.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.gdx.rainbow.Selectable;
import com.gdx.rainbow.screens.upgrade.stats.Stats;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.screens.GameScreen;

/**
 * Created by WAM on 9/5/2016.
 */
public class Player extends Object {

    public static final float SIZE = .25f;

    public float blowTimer = 0;

    public float yScl = 1;
    public float xScl = 1;
    public float sclOff = 1;

    public Stats stats;

    public Player() {
        super();
        density = 1f;
        restitution = .8f;
        width = SIZE;
        height = SIZE;
        start_image = Assets.player_default_image;
        categoryMask = Object.MASK_PLAYER;
        categoryBit = Object.CATEGORY_PLAYER;
    }

    public void manipulateCloud(Cloud c, Selectable character) {
        if (character == Selectable.CHARACTER_DEFUALT) {
            Vector2 force = new Vector2(0, 0);
            Vector2 startLocation = c.body.getPosition();
            Vector2 endLocation = body.getPosition();
            //vector between desination and player;
            float dx = endLocation.x - startLocation.x;
            float dy = endLocation.y - startLocation.y;
            float xDir = 1;
            float yDir = 1;
            if (dx < 0) xDir = -1;
            if (dy < 0) yDir = -1;
            if (Math.abs(dx) < .4f) dx = .4f * xDir;
            if (Math.abs(dy) < .4f) dy = .4f * yDir;
            force.set(1 / dx, 1 / dy);
            force.scl(-.2f * .4f);
            force.scl(1, .5f);
            force.scl(stats.pushStrength);
            c.body.applyForce(force, c.body.getPosition(), true);
            force.scl(1 / stats.pushStrength);
            //Push Player away from cloud
            force.scl(-stats.pushBackForce);
            body.applyForce(force, body.getPosition(), true);
        }

        if (character == Selectable.CHARACTER_STORM) {
            Vector2 force = new Vector2(0, 0);
            Vector2 startLocation = c.body.getPosition();
            Vector2 endLocation = body.getPosition();
            //vector between desination and player;
            float dx = endLocation.x - startLocation.x;
            float dy = endLocation.y - startLocation.y;
            float xDir = 1;
            float yDir = 1;
            if (dx < 0) xDir = -1;
            if (dy < 0) yDir = -1;
            if (Math.abs(dx) < .4f) dx = .4f * xDir;
            if (Math.abs(dy) < .4f) dy = .4f * yDir;
            force.set(dx, dy);
            force.scl(.2f * .4f);
            c.body.applyForce(force, c.body.getPosition(), true);
        }

    }

    public void set(GameScreen gameScreen, float x, float y, Stats stats) {
        this.stats = stats;
        super.set(gameScreen, x, y);

    }

}
