package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionComponent implements Component {

    public static byte OBSTACLE = 1;
    public static byte ZINDEX = 2;
    public static byte ITEM = 4;
    public static byte MAPINTERACTION = 8;
    public static byte CHARACTER = 16;

    public Shape mShape;
    public ICollisionHandler mHandler;
    public Byte mType;
    public String mName;
    public Object mData;


    public CollisionComponent() {
    }

    public CollisionComponent(byte aType, Shape shape, String aName, Object aData, ICollisionHandler aHandler) {
        mShape = shape;
        mHandler = aHandler;
        mType=aType;
        mName=aName;
        mData = aData;
    }
}
