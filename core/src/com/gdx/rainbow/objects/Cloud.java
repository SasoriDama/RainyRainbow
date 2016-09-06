package com.gdx.rainbow.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gdx.rainbow.Assets;

/**
 * Created by WAM on 9/5/2016.
 */
public class Cloud extends Object {

    public Cloud() {
        super();
    }

    public void set(World world, float x, float y, Vector2 initialVel) {
        super.set(world, x, y);

        setSprite(Assets.cloud_image);

        body.setLinearVelocity(initialVel);
    }

    protected void configBodyDef() {
        bodyDef.type = BodyType.DynamicBody;
    }

    protected void configFixtureDef() {
        PolygonShape box = new PolygonShape();
        box.setAsBox(768/4, 356/4);

        fixtureDef.shape = box;
        fixtureDef.density = .5f;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = .6f;

    }

}
