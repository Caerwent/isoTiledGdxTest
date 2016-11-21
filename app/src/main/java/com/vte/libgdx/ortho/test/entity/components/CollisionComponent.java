package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Polygon;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionComponent implements Component {
    static public enum Type {
        OBSTACLE,
        ZINDEX,
        ITEM,
        MAPINTERACTION,
        CHARACTER
    };

    public Shape mShape;
    public ICollisionHandler mHandler;
    public Type mType;
    public String mName;


    public CollisionComponent() {
    }

    public CollisionComponent(Type aType, Shape shape, String aName, ICollisionHandler aHandler) {
        mShape = shape;
        mHandler = aHandler;
        mType=aType;
        mName=aName;
    }
}
