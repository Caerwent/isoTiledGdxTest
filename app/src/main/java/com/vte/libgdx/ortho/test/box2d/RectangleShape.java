package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gwalarn on 20/11/16.
 */

public class RectangleShape extends Shape<Rectangle> {

    protected Rectangle mRect = new Rectangle();

    public RectangleShape()
    {
        mType = Type.RECT;
        mRect = new Rectangle();
    }
    @Override
    public Rectangle getShape() {
        return mRect;
    }
    @Override
    public void setShape(Rectangle aShape)
    {
        mRect = aShape;
    }
    @Override
    public float getX()
    {
        return mRect.x;
    }
    @Override
    public float getY()
    {
        return mRect.y;
    }

    @Override
    public void setX(float x)
    {
        mRect.x = x;
    }
    @Override
    public void setY(float y)
    {
        mRect.y = y;
    }
    @Override
    public Rectangle getBounds()
    {

        return mRect;
    }
    @Override
    public Shape clone()
    {
        RectangleShape clone = new RectangleShape();
        clone.getShape().set(mRect);
        return clone;
    }

    @Override
    public float getYAtX(float x) {
        if (x >= mRect.x && x <= (mRect.x + mRect.getWidth())) {
            return mRect.y;
        }
        return -1;
    }

}
