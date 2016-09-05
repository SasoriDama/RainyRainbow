package com.gdx.rainbow.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by WAM on 9/5/2016.
 */
public class Object {

    public Body body;
    protected BodyDef bodyDef;
    protected FixtureDef fixtureDef;

    public Object(World world) {
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
    }

}
