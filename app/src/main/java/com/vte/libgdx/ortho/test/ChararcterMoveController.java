package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.PathHero;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 01/07/2016.
 */

public class ChararcterMoveController extends InputAdapter {
    final OrthographicCamera camera;
    final Vector3 mCursorPoint = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();
    Circle mPathSpot;
    Circle mTouchSpot;

    Vector2 mLastPoint = new Vector2();
    Vector2 mTouchSpotPoint = new Vector2();
    Vector2 mPathSpotPoint = new Vector2();

    PathHero path;
    public boolean isActive = false;
    private GameMap mMap;


    public ChararcterMoveController(OrthographicCamera camera) {
        this.camera = camera;


    }

    public void setMap(GameMap aMap) {
        mMap = aMap;
        float radius = Math.max(mMap.getPlayer().getHero().getShape().getBounds().getWidth(), mMap.getPlayer().getHero().getShape().getBounds().getHeight()) / 2;
        mPathSpot = new Circle(0, 0, radius);
        mTouchSpot = new Circle(0, 0, radius);
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (!isActive)
            return false;
        camera.unproject(mCursorPoint.set(x, y, 0));

        if (mMap!=null && !(last.x == -1 && last.y == -1 && last.z == -1)) {
            camera.unproject(delta.set(last.x, last.y, 0));
            delta.sub(mCursorPoint);

            if (Math.sqrt(delta.x * delta.x + delta.y * delta.y) < 0.1)
                return true;


            Vector2 nextPoint = null;
            Vector2 tmpNextPoint = null;

            mTouchSpotPoint.set(mCursorPoint.x, mCursorPoint.y);
            mTouchSpot.setPosition(mTouchSpotPoint.x, mTouchSpotPoint.y);
            mPathSpot.setPosition(mLastPoint.x - delta.x, mLastPoint.y - delta.y);
            mPathSpotPoint.set(mPathSpot.x, mPathSpot.y);
            Vector2 intersection = new Vector2();

            Array<Shape> collisions = mMap.getBodiesCollision();
            //Gdx.app.debug("DEBUG", "mTouchSpot=(" + mTouchSpot.x + "," + mTouchSpot.y + ") w="+mTouchSpot.getWidth());
            //Gdx.app.debug("DEBUG", "mPathSpot=(" + mPathSpot.x + "," + mPathSpot.y + ") w="+mPathSpot.getWidth());
            if (collisions != null && collisions.size > 0) {
                for (Shape shape : collisions) {
                    if (shape.getType() != Shape.Type.POLYGON)
                        continue;
                    Polygon poly = ((PolygonShape) shape).getShape();
                    //Gdx.app.debug("DEBUG", "Check collision with poly" + ShapeUtils.logPoly(poly));
                    //Gdx.app.debug("DEBUG", "segment (mLastPoint, mTouchSpotPoint)=[(" + mLastPoint.x + "," + mLastPoint.y + ")" + "(" + mTouchSpotPoint.x + "," + mTouchSpotPoint.y + ")]");


                    // search for the nearest polygon segment on last point and touch point segment
                    if (!Intersector.intersectSegmentPolygon(mLastPoint, mTouchSpotPoint, poly) &&
                            !ShapeUtils.overlaps(poly, mTouchSpot)) {
                        tmpNextPoint = mTouchSpotPoint;
                        //     Gdx.app.debug("DEBUG", "segment not intersect");
                    } else if (!ShapeUtils.overlaps(poly, mPathSpot)) {
                        tmpNextPoint = mPathSpotPoint;
                        //     Gdx.app.debug("DEBUG", "mPathSpot not overlaps");
                    } else {
                        //     Gdx.app.debug("DEBUG", "mPathSpot overlaps");
                        tmpNextPoint = null;

                        intersection.set(mTouchSpotPoint.x, mLastPoint.y);
                        //    Gdx.app.debug("DEBUG", "check using next point =(" + intersection.x + "," + intersection.y + ")");
                        if (!Intersector.intersectSegmentPolygon(mLastPoint, intersection, poly)) {
                            nextPoint = intersection;
                            //        Gdx.app.debug("DEBUG", "nextPoint found");
                            break;
                        }

                        intersection.set(mLastPoint.x, mTouchSpotPoint.y);
                        //    Gdx.app.debug("DEBUG", "check using next point =(" + intersection.x + "," + intersection.y + ")");
                        if (!Intersector.intersectSegmentPolygon(mLastPoint, intersection, poly)) {
                            nextPoint = intersection;
                            //        Gdx.app.debug("DEBUG", "nextPoint found");
                            break;
                        }

                    }
                    // Gdx.app.debug("DEBUG", "intersection not found, continue with next polygon");

                }
                if (nextPoint == null && tmpNextPoint != null)
                    nextPoint = tmpNextPoint;

            } else {
                nextPoint = mTouchSpotPoint;
            }

            if (nextPoint != null) {

                if (nextPoint.dst2(mLastPoint) > PathHero.CHECK_RADIUS) {
                    path.addPoint(nextPoint.x, nextPoint.y);
                    mLastPoint.set(nextPoint);
                    //  mTouchSpotPoint.set(mLastPoint.x, mLastPoint.y);
                    //   Gdx.app.debug("DEBUG", "add pointX=" + mLastPoint.x + " pointY=" + mLastPoint.y);
                }


            }
            //Gdx.app.debug("DEBUG", "add pointX=" + point2d.x + " pointY=" + point2d.y);

            //mBob.setPosition(x, y, curr.x/ SCALE_FACTOR, curr.y/ SCALE_FACTOR);

        }
        last.set(x, y, 0);
        return true;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {

        camera.unproject(mCursorPoint.set(x, y, 0));
        if (mMap!=null && mMap.getPlayer()!=null && mMap.getPlayer().getHero()!=null &&  mMap.getPlayer().getHero().getShape().getBounds().contains(mCursorPoint.x, mCursorPoint.y)) {
            if (path != null)
                path.destroy();
            path = new PathHero();
            Rectangle bobBound = mMap.getPlayer().getHero().getShape().getBounds();
            Vector2 bobPos = mMap.getPlayer().getHero().getPosition();
//            mCursorPoint.set(.x, mBob.getPosition().y, mCursorPoint.z);
            path.addPoint(bobPos.x, bobPos.y);
            mLastPoint.set(mCursorPoint.x, mCursorPoint.y);
            mTouchSpotPoint.set(mCursorPoint.x, mCursorPoint.y);
            mPathSpot.setPosition(mCursorPoint.x, mCursorPoint.y);
            mTouchSpot.setPosition(mCursorPoint.x, mCursorPoint.y);
            isActive = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        last.set(-1, -1, -1);
        if (mMap!=null && mMap.getPlayer()!=null && mMap.getPlayer().getHero()!=null && isActive) {
            isActive = false;
            mMap.getPlayer().getHero().setPath(path);
        }
        return false;
    }


}