package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gwalarn on 20/11/16.
 */

public class CircleShape extends Shape<Circle> {

    protected Circle mCircle;
    protected Rectangle mBounds = new Rectangle();

    public CircleShape()
    {
        mType = Type.CIRCLE;
        mCircle = new Circle();
    }
    @Override
    public Circle getShape() {
        return mCircle;
    }
    @Override
    public void setShape(Circle aShape)
    {
        mCircle = aShape;
    }
    @Override
    public float getX()
    {
        return mCircle.x;
    }
    @Override
    public float getY()
    {
        return mCircle.y;
    }
    public void setRadius(float radius)
    {
        mCircle.setRadius(radius);
        mBounds.setWidth(radius);
        mBounds.setHeight(radius);
    }
    @Override
    public void setX(float x)
    {
        mCircle.x = x;
        mBounds.setPosition(mCircle.x, mCircle.y);
    }
    @Override
    public void setY(float y)
    {
        mCircle.y = y;
    }
    @Override
    public Rectangle getBounds()
    {

        return mBounds;
    }
    @Override
    public Shape clone()
    {
        CircleShape clone = new CircleShape();
        clone.setRadius(mCircle.radius);
        clone.setX(getX());
        clone.setY(getY());
        return clone;
    }
}
