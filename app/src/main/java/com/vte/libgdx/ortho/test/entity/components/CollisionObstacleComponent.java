package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.box2d.Shape;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionObstacleComponent implements Component {

    public static byte OBSTACLE = 1;
    public static byte MAPINTERACTION = 2;
    public static byte ITEM = 4;
    public static byte HERO = 8;


    public Shape mShape;
    public ICollisionObstacleHandler mHandler;
    public byte mType;
    public String mName;
    public Object mData;


    public CollisionObstacleComponent() {
    }

    public CollisionObstacleComponent(byte aType, Shape shape, String aName, Object aData, ICollisionObstacleHandler aHandler) {
        mShape = shape;
        mHandler = aHandler;
        mType=aType;
        mName=aName;
        mData = aData;
    }
}
