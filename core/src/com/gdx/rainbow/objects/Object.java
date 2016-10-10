package com.gdx.rainbow.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.GameScreen;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

/**
 * Created by WAM on 9/5/2016.
 */
public class Object {

    public static final int PLAYER = 0, CLOUD = 1, DENSE_CLOUD = 2, POWER_UP = 3;

    //Collision Filters
    public static final short CATEGORY_PLAYER = 0x0001;
    public static final short CATEGORY_CLOUD = 0x0002;
    public static final short CATEGORY_DENSE_CLOUD = 0x004;

    public static final short MASK_PLAYER = 0;
    public static final short MASK_CLOUD = -4;
    public static final short MASK__DENSE_CLOUD = -2;
    //if you want clouds to collide with eachother set MASK_CLOUD to CATEGORY_CLOUD;

    public Body body;

    protected Fixture fixture;
    protected BodyDef bodyDef;
    protected FixtureDef fixtureDef;

    protected float density = .5f;
    protected short categoryMask, categoryBit;
    public Box2DSprite image;

   private Box2DSprite sprite;

    public float timerOffset;

    public static Object createObject(int ID) {
        switch (ID) {
            case (Object.PLAYER): { return new Player();}
            case (Object.CLOUD): { return new Cloud();}
            case (Object.DENSE_CLOUD): {return new DenseCloud();}
            case (Object.POWER_UP): {return new PowerUp();}
            default: return null;
        }
    }

    public Object() {
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        timerOffset = MathUtils.random(10f);
    }

    public void set(GameScreen gameScreen, float x, float y) {

        configBodyDef();
        setPosition(x, y);
        body = gameScreen.world.createBody(bodyDef);
        configFixtureDef();
        fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        gameScreen.objects.add(this);

        this.setSprite(image);
    }

    protected void configBodyDef() {

    }

    protected void configFixtureDef() {

    }

    protected void setPosition(float x, float y) {
        bodyDef.position.set(x, y);
    }

    public Box2DSprite getSprite() {
        return sprite;
    }

    public void setSprite(Box2DSprite sprite) {
        this.sprite = sprite;
        fixture.setUserData(sprite);
        body.setUserData(sprite);
    }

}
