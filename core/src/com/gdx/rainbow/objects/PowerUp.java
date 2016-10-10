package com.gdx.rainbow.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.gdx.rainbow.Assets;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

/**
 * Created by WAM on 10/7/2016.
 */

public class PowerUp extends Object {

    public PowerUp() {
        super();
        this.density = 1;
        image = new Box2DSprite(Assets.sun_timer_image);
    }

    @Override
    protected void configBodyDef() {
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    @Override
    protected void configFixtureDef() {
        PolygonShape box = new PolygonShape();
        box.setAsBox(.25f, .25f);

        //setSprite(image);
        fixtureDef.shape = box;
        fixtureDef.density = this.density;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = categoryMask;
        fixtureDef.filter.maskBits = categoryBit;

    }

}
