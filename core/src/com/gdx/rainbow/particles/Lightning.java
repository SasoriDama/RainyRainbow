package com.gdx.rainbow.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.gdx.rainbow.Assets;

/**
 * Created by WAM on 10/10/2016.
 */
public class Lightning {

    //Lightning Things
    private float nextLightningTimer = 0;
    private float nextLightningTime = 60;

    public float maxNextLightningTime;

    private float drawLightningTimer = 0;
    private float drawLightningTime = .36f;

    public void update(float delta) {
        nextLightningTimer += delta;

        if (nextLightningTimer >= nextLightningTime) {

            drawLightningTimer += delta;

            if (drawLightningTimer >= drawLightningTime) {
                nextLightningTime = MathUtils.random(7f, maxNextLightningTime);
                nextLightningTimer = 0;
                drawLightningTimer = 0;
                Assets.thunder_sound.play();
            }

        }

    }

    public void createLightning() {
        this.nextLightningTimer = this.nextLightningTime;
    }

    public void drawLightning(SpriteBatch batch) {
        if (drawLightningTimer == 0) return;
        float per = drawLightningTimer/drawLightningTime;
        if (((int) (per * 100)) % 4 == 0) return;
        TextureRegion r = new TextureRegion(Assets.lightning_image);
        batch.setColor(1, 1, .87f, per);
        batch.draw(r, -r.getRegionWidth()/2, -r.getRegionHeight()/2);
        batch.setColor(1, 1, 1, 1);
    }

}
