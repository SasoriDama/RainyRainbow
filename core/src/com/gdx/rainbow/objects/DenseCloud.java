package com.gdx.rainbow.objects;

import com.badlogic.gdx.math.Vector2;
import com.gdx.rainbow.Assets;
import com.gdx.rainbow.GameScreen;

/**
 * Created by WAM on 9/25/2016.
 */
public class DenseCloud extends Cloud {

    public DenseCloud() {
        super();
        density = 2.5f;
        image = Assets.dense_cloud_image;
        categoryMask = Object.MASK__DENSE_CLOUD;
        categoryBit = Object.CATEGORY_DENSE_CLOUD;
    }

}
