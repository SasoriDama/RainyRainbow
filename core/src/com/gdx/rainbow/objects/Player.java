package com.gdx.rainbow.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.GameScreen;

/**
 * Created by WAM on 9/5/2016.
 */
public class Player extends Object {

    public static final float SIZE = .25f;//.2f

    public Player() {
        super();

    }

    public void set(GameScreen gameScreen, float x, float y) {
        super.set(gameScreen, x, y);
        setSprite(Assets.player_image);

    }

    protected void configBodyDef() {
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    protected void configFixtureDef() {
        PolygonShape box = new PolygonShape();
        //box.setAsBox((256)/10, (256)/10);
        box.setAsBox(SIZE, SIZE);

        fixtureDef.shape = box;
        //fixtureDef.density = .05f;
        fixtureDef.density = 1;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = .8f;
        fixtureDef.filter.categoryBits = Object.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = Object.MASK_PLAYER;
    }

}
