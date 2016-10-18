package com.gdx.rainbow.screens.menus.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by WAM on 9/25/2016.
 */
public class Button {

    Texture t;
    public Texture clickedImage;
    float x;
    float y;
    float scl;
    public String text = "";

    public boolean pressed = false;

    public Rectangle bounds;

    public Button(float x, float y, float scl, Texture t, String text) {
        this.x = x;
        this.y = y;
        this.t = t;
        this.scl = scl;
        bounds = new Rectangle(x - t.getWidth() * scl/2, y - t.getHeight() * scl/2, t.getWidth()* scl, t.getHeight() * scl);
        //sprite.setScale(scl);
        this.text = text;
    }

    public Button(float x, float y, float scl, Texture t) {
        this.x = x;
        this.y = y;
        this.t = t;
        this.scl = scl;
        bounds = new Rectangle(x - t.getWidth() * scl/2, y - t.getHeight() * scl/2, t.getWidth()* scl, t.getHeight() * scl);
    }

    public boolean isPressed() {

        boolean a = false;

        if (pressed) {
            a = true;
            pressed = false;
        }

        if (clickedImage != null) {
            Texture r = t;
            if (a) {
                t = clickedImage;
            }
            else {
                t = r;
            }
        }


        return a;
    }

    public boolean containsPoint(float x, float y) {
        return bounds.contains(x, y);
    }

    public void draw(SpriteBatch batch, BitmapFont font) {
        TextureRegion r = new TextureRegion(t);
        batch.draw(r, x - r.getRegionWidth()/2, y - r.getRegionHeight()/2, r.getRegionWidth()/2, r.getRegionHeight()/2, r.getRegionWidth(), r.getRegionHeight(), scl, scl, 0);
        font.draw(batch, text, x - (t.getWidth() * scl/4), y);
    }

}
