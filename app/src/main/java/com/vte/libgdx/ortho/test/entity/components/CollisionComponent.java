package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Polygon;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionComponent implements Component {
    public Polygon mBound;
    public ICollisionHandler mHandler;


    public CollisionComponent() {
    }

    public CollisionComponent(Polygon boundingPolygon, ICollisionHandler aHandler) {
        mBound = boundingPolygon;
        mHandler = aHandler;
    }
}
