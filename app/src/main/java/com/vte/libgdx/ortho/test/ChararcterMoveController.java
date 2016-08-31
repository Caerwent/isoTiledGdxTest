package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;
import com.vte.libgdx.ortho.test.box2d.Path;

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

    Bob mBob;
    Path path;
    public boolean isActive = false;

    public ChararcterMoveController(OrthographicCamera camera, Bob aBob) {
        this.camera = camera;
        mBob = aBob;
        mPathSpot = new Circle(0, 0, mBob.getBound().getWidth() / 2);
        mTouchSpot = new Circle(0, 0, mBob.getBound().getWidth() / 2);

    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (!isActive)
            return false;
        camera.unproject(mCursorPoint.set(x, y, 0));

        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
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
            if(mTouchSpotPoint.y>13)
            {
                int debug=0;
                debug=1;
            }
            Array<Polygon> collisions = MapBodyManager.getInstance().getBodiesCollision();
            Gdx.app.debug("DEBUG", "mTouchSpot=(" + mTouchSpot.x + "," + mTouchSpot.y + ") Rx="+mTouchSpot.radius);
            Gdx.app.debug("DEBUG", "mPathSpot=(" + mPathSpot.x + "," + mPathSpot.y + ") Rx="+mPathSpot.radius);
            if (collisions != null && collisions.size>0) {
                for (Polygon poly : collisions) {

                    Gdx.app.debug("DEBUG", "Check collision with poly" + logPoly(poly));
                    Gdx.app.debug("DEBUG", "segment (mLastPoint, mTouchSpotPoint)=[(" + mLastPoint.x + "," + mLastPoint.y + ")" + "(" + mTouchSpotPoint.x + "," + mTouchSpotPoint.y + ")]");


                    // search for the nearest polygon segment on last point and touch point segment
                    if (!Intersector.intersectSegmentPolygon(mLastPoint, mTouchSpotPoint, poly) &&
                            !overlaps(poly, mTouchSpot)) {
                        tmpNextPoint = mTouchSpotPoint;
                        Gdx.app.debug("DEBUG", "segment not intersect");
                    } else if (!overlaps(poly, mPathSpot)) {
                        tmpNextPoint = mPathSpotPoint;
                        Gdx.app.debug("DEBUG", "mPathSpot not overlaps");
                    } else {
                        Gdx.app.debug("DEBUG", "mPathSpot overlaps");
                        tmpNextPoint=null;

                        intersection.set(mTouchSpotPoint.x, mLastPoint.y);
                        Gdx.app.debug("DEBUG", "check using next point =(" + intersection.x + "," + intersection.y + ")");
                        if (!Intersector.intersectSegmentPolygon(mLastPoint, intersection, poly)) {
                            nextPoint = intersection;
                            Gdx.app.debug("DEBUG", "nextPoint found");
                            break;
                        }

                        intersection.set(mLastPoint.x, mTouchSpotPoint.y);
                        Gdx.app.debug("DEBUG", "check using next point =(" + intersection.x + "," + intersection.y + ")");
                        if (!Intersector.intersectSegmentPolygon(mLastPoint, intersection, poly)) {
                            nextPoint = intersection;
                            Gdx.app.debug("DEBUG", "nextPoint found");
                            break;
                        }

                     }
                     Gdx.app.debug("DEBUG", "intersection not found, continue with next polygon");

                }
                if(nextPoint==null && tmpNextPoint!=null)
                    nextPoint=tmpNextPoint;

            } else {
                nextPoint = mTouchSpotPoint;
            }

            if (nextPoint != null) {

                if (nextPoint.dst2(mLastPoint) > Path.CHECK_RADIUS) {
                    path.AddPoint(nextPoint.x, nextPoint.y, 0.2f);
                    mLastPoint.set(nextPoint);
                    //  mTouchSpotPoint.set(mLastPoint.x, mLastPoint.y);
                     Gdx.app.debug("DEBUG", "add pointX=" + mLastPoint.x + " pointY=" + mLastPoint.y);
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

        if (mBob.getBound().contains(mCursorPoint.x, mCursorPoint.y)) {
            if (path != null)
                path.destroy();
            path = new Path();
            Rectangle bobBound = mBob.getBound();
            Vector2 bobPos =mBob.getPosition();
//            mCursorPoint.set(.x, mBob.getPosition().y, mCursorPoint.z);
            path.AddPoint(bobPos.x, bobPos.y, 0.2f);
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
        if (isActive) {
            isActive = false;
            mBob.SetPath(path);
        }
        return false;
    }

    /**
     * Get projected point of P on line v1,v2. Faster version. (http://www.sunshine2k.de/coding/java/PointOnLine/PointOnLine.html)
     *
     * @return projected point p.
     */
    public Vector2 getProjectedPointOnLineFast(float vx1, float vy1, float vx2, float vy2, float px, float py) {
        // get dot product of e1, e2
        Vector2 e1 = new Vector2(vx2 - vx1, vy2 - vy1);
        Vector2 e2 = new Vector2(px - vx1, py - vy1);
        double valDp = e1.x * e2.x + e1.y * e2.y;
        // get squared length of e1
        double len2 = e1.x * e1.x + e1.y * e1.y;
        Vector2 p = new Vector2((float) (vx1 + (valDp * e1.x) / len2),
                (float) (vy1 + (valDp * e1.y) / len2));
        return p;
    }

    /**
     * Check if p', the projected point of P onto line v1,v2 is on the line segment.(http://www.sunshine2k.de/coding/java/PointOnLine/PointOnLine.html)
     *
     * @return -1 if p' before v1
     * 0 if p' is v1
     * 1 if p' is between v1 and v2
     * 2 if p' is v2
     * 3 if p' is after v2
     */
    public int isProjectedPointOnLineSegment(float vx1, float vy1, float vx2, float vy2, float px, float py) {
        //e1 = (v1, v2) and e2 = (p, v1)
        //If p' matches with v1 (this means both have the same location), then
        //DP(e1, e2) = 0. e1 is then perpendicular to e2.
        //On the other side, if p' matches with v2 (thus p' is the same as v2), then
        //DP(e1, e2) = DP(e1, e1) = e1.x^2 + e1.y^2.
        //If p' lies somewhere between v1 and v2, then for the dot-product of e1 and e2 applies:
        //0 <= DP(e1,e2) <= e1.x^2 + e1.y^2.

        Vector2 e1 = new Vector2(vx1 - vx2, vy1 - vy2);
        Vector2 e2 = new Vector2(px - vx1, py - vy1);
        double recArea = dotProduct(e1, e1);
        double val = dotProduct(e1, e2);
        if (val < 0)
            return -1;
        else if (val == 0)
            return 0;
        else if (val > 0 && val < recArea)
            return 1;
        else if (val == recArea)
            return 2;
        else
            return 3;
    }

    public double dotProduct(Vector2 e1, Vector2 e2) {
        return e1.x * e2.x + e1.y * e2.y;
    }

    public int getNextVertice(float[] vertices, int currIdx, float[] nextVertices) {
        int idx1, idx2, idx3, idx4;
        currIdx += 2;

        idx1 = currIdx;
        idx2 = currIdx + 1;
        if (currIdx >= vertices.length - 2) {
            idx3 = 0;
            idx4 = 1;
        } else {
            idx3 = currIdx + 2;
            idx4 = currIdx + 3;
        }
        nextVertices[0] = vertices[idx1];
        nextVertices[1] = vertices[idx2];
        nextVertices[2] = vertices[idx3];
        nextVertices[3] = vertices[idx4];
        return currIdx;
    }

    public int getPreviousVertice(float[] vertices, int currIdx, float[] nextVertices) {
        int idx1, idx2, idx3, idx4;
        idx3 = currIdx;
        idx4 = currIdx + 1;
        if (currIdx < 2) {
            idx1 = vertices.length - 2;
            idx2 = vertices.length - 1;
        } else {
            idx1 = currIdx - 2;
            idx2 = currIdx - 1;

        }

        currIdx = idx1;

        nextVertices[0] = vertices[idx1];
        nextVertices[1] = vertices[idx2];
        nextVertices[2] = vertices[idx3];
        nextVertices[3] = vertices[idx4];
        return currIdx;
    }

    public boolean overlaps(Polygon polygon, Circle circle) {
        float[] vertices = polygon.getTransformedVertices();
        Vector2 center = new Vector2(circle.x, circle.y);
        Vector2 displacement = new Vector2();
        float squareRadius = circle.radius * circle.radius;
        boolean ovelaps = false;


        if(Intersector.isPointInPolygon(vertices, 0, vertices.length, center.x, center.y))
        {
            ovelaps = true;
        }
        else {
            for (int i = 0; i < vertices.length; i += 2) {
                if (i == 0) {
                    if (Intersector.intersectSegmentCircleDisplace(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius, displacement) != Float.POSITIVE_INFINITY) {
                        ovelaps = true;
                    }
                } else {
                    if (Intersector.intersectSegmentCircleDisplace(new Vector2(vertices[i - 2], vertices[i - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius, displacement) != Float.POSITIVE_INFINITY) {
                        ovelaps = true;
                    }
                }
            }
        }
        return ovelaps;
    }

    private String logPoly(Polygon aPoly) {
        float[] vertices = aPoly.getTransformedVertices();
        String logStr = "[";
        for (int i = 0; i < vertices.length - 2; i += 2) {
            logStr += "(" + vertices[i] + "," + vertices[i + 1] + ") ";
        }
        logStr += "]";
        return logStr;
    }
}