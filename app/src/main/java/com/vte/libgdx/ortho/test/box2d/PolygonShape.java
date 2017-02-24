package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gwalarn on 20/11/16.
 */

public class PolygonShape extends Shape<Polygon> {

    protected Polygon mPoly;

    public PolygonShape()
    {
        mType = Type.POLYGON;
        mPoly = new Polygon();
    }
    @Override
    public Polygon getShape() {
        return mPoly;
    }
    @Override
    public void setShape(Polygon aShape)
    {
        mPoly = aShape;
    }
    @Override
    public float getX() {
        return mPoly.getX();
    }

    @Override
    public float getY() {
        return mPoly.getY();
    }

    @Override
    public void setX(float x) {
        mPoly.setPosition(x, mPoly.getY());
    }

    @Override
    public void setY(float y) {
        mPoly.setPosition(mPoly.getX(), y);
    }

    @Override
    public Rectangle getBounds() {
        return mPoly.getBoundingRectangle();
    }

    @Override
    public Shape clone()
    {
        PolygonShape clone = new PolygonShape();
        clone.getShape().setVertices(mPoly.getVertices());
        clone.getShape().setOrigin(mPoly.getOriginX(), mPoly.getOriginY());
        clone.setX(getX());
        clone.setY(getY());
        return clone;
    }
}
