package com.gdx.rainbow.objects;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Created by WAM on 9/5/2016.
 */
public class Cloud extends Object {

    public Cloud(World world) {
        super(world);

        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(0, 0);

        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

        fixtureDef.shape = circle;
        fixtureDef.density = .5f;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = .6f;

        Fixture fixture = body.createFixture(fixtureDef);
    }

}
