package com.gdx.rainbow.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.rainbow.Assets;

/**
 * Created by WAM on 9/5/2016.
 */
public class Player extends Object {

    public Player() {
        super();

    }

    public void set(World world, float x, float y) {
        super.set(world, x, y);
        setSprite(Assets.player_image);

    }

    protected void configBodyDef() {
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    protected void configFixtureDef() {
        PolygonShape box = new PolygonShape();
        box.setAsBox(256/5, 256/5);

        fixtureDef.shape = box;
        fixtureDef.density = .5f;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = .6f;

    }

}
