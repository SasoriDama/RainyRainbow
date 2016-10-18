package com.gdx.rainbow.objects;

import com.gdx.rainbow.Assets;

/**
 * Created by WAM on 9/25/2016.
 */
public class DenseCloud extends Cloud {

    public DenseCloud() {
        super();
        density = 2.5f;
        start_image = Assets.dense_cloud_image;
        categoryMask = Object.MASK__DENSE_CLOUD;
        categoryBit = Object.CATEGORY_DENSE_CLOUD;
    }

}
