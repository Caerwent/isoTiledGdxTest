package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Polygon;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionComponent implements Component {
    static public enum Type {
        OBSTACLE,
        ZINDEX,
        ITEM,
        BOB
    };

    public Polygon mBound;
    public ICollisionHandler mHandler;
    public Type mType;


    public CollisionComponent() {
    }

    public CollisionComponent(Type aType, Polygon boundingPolygon, ICollisionHandler aHandler) {
        mBound = boundingPolygon;
        mHandler = aHandler;
        mType=aType;
    }
}
