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
    Circle touchSpot = new Circle(0, 0, 2);

    Vector2 mLastPoint = new Vector2();
    Vector2 mCurrentPoint = new Vector2();
    Vector2 mBoundedCurrentPoint = new Vector2();

    Bob mBob;
    Path path;
    Polygon mPosPoly;
    public boolean isActive = false;

    public ChararcterMoveController(OrthographicCamera camera, Bob aBob) {
        this.camera = camera;
        mBob = aBob;
        mPosPoly = new Polygon();
        float[] vertices = new float[8];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = mBob.getBound().getWidth();
        vertices[3] = 0;
        vertices[4] = mBob.getBound().getWidth();
        vertices[5] = mBob.getBound().getHeight();
        vertices[6] = 0;
        vertices[7] = mBob.getBound().getHeight();
        mPosPoly.setVertices(vertices);

    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (!isActive)
            return false;
        camera.unproject(mCursorPoint.set(x, y, 0));

        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
            camera.unproject(delta.set(last.x, last.y, 0));
            delta.sub(mCursorPoint);


            Vector2 nextPoint = null;
            float dx= -delta.x;
            float dy= -delta.y;
            if(delta.x<0)
            {
                dx=+mBob.getBound().getWidth();
            }
            if(delta.y<0)
            {
                dy=+mBob.getBound().getHeight();
            }
            mCurrentPoint.set(mCurrentPoint.x -delta.x, mCurrentPoint.y -delta.y);
            mBoundedCurrentPoint.set(mCurrentPoint.x +dx, mCurrentPoint.y +dy);
            mPosPoly.setPosition(mCurrentPoint.x, mCurrentPoint.y);
            Vector2 intersection = new Vector2();
            Vector2 nearestIntersection = new Vector2();
            float[] nearestVertice = new float[4];
            float[] collisionPolyVertice = null;
            int nearestVerticeOffset = 0;
            boolean hasCollision = false;
            Array<Polygon> collisions = MapBodyManager.getInstance().getBodiesCollision();
            if (collisions != null) {
                float[] vertices = null;
                for (Polygon poly : collisions) {
                    vertices = poly.getTransformedVertices();

                    // search for the nearest polygon segment on last point and touch point segment
                    if (!Intersector.intersectSegmentPolygon(mLastPoint, mBoundedCurrentPoint, poly) ) {
                        nextPoint = mCurrentPoint;
                    } else {
                        if (!Intersector.isPointInPolygon(vertices, 0, vertices.length, mBoundedCurrentPoint.x, mBoundedCurrentPoint.y)) {
                            nextPoint = mCurrentPoint;
                        } else {
                            float nearestDist = Float.MAX_VALUE;
                            hasCollision = true;
                            int i = -2;
                            boolean endLoop = false;
                            int idx1, idx2, idx3, idx4;
                            while (!endLoop) {
                                i += 2;
                                idx1 = i;
                                idx2 = i + 1;
                                if (i >= vertices.length - 2) {
                                    idx3 = 0;
                                    idx4 = 1;
                                    endLoop = true;
                                } else {
                                    idx3 = i + 2;
                                    idx4 = i + 3;
                                }

                                Intersector.nearestSegmentPoint(vertices[idx1], vertices[idx2], vertices[idx3], vertices[idx4],
                                        mBoundedCurrentPoint.x, mBoundedCurrentPoint.y, intersection);
                                float dist = intersection.dst(mBoundedCurrentPoint.x, mBoundedCurrentPoint.y);
                                if (dist < nearestDist) {
                                    nearestDist = dist;
                                    nearestVerticeOffset = idx1;
                                    nearestVertice[0] = vertices[idx1];
                                    nearestVertice[1] = vertices[idx2];
                                    nearestVertice[2] = vertices[idx3];
                                    nearestVertice[3] = vertices[idx4];
                                    nearestIntersection.set(intersection.x, intersection.y);
                                    collisionPolyVertice = vertices;

                                }
                            }
                        }


                    }
                }
                if (hasCollision && collisionPolyVertice != null) {
                    float[] nextPrevVertice = new float[4];

                    nextPrevVertice[0] = nearestVertice[0];
                    nextPrevVertice[1] = nearestVertice[1];
                    nextPrevVertice[2] = nearestVertice[2];
                    nextPrevVertice[3] = nearestVertice[3];
                    //Gdx.app.debug("DEBUG", "collision with vertice [" + nextPrevVertice[0] + "," + nextPrevVertice[1] + " - " + nextPrevVertice[2] + "," + nextPrevVertice[3] + "]");

                    int vertIdx = nearestVerticeOffset;
                    int nextIter = 2;
                    while (nextIter > 0) {
                        int projDir = isProjectedPointOnLineSegment(nextPrevVertice[0], nextPrevVertice[1], nextPrevVertice[2], nextPrevVertice[3], mCurrentPoint.x, mCurrentPoint.y);
                        if (projDir <= 0) {
                            if (mLastPoint.x == nextPrevVertice[0] && mLastPoint.y == nextPrevVertice[1]) {
                                // proj on previous vertices
                                vertIdx = getPreviousVertice(collisionPolyVertice, vertIdx, nextPrevVertice);
                                Gdx.app.debug("DEBUG", "projDir=" + projDir + " prev vertice [" + nextPrevVertice[0] + "," + nextPrevVertice[1] + " - " + nextPrevVertice[2] + "," + nextPrevVertice[3] + "]");
                                nextIter--;
                            } else {
                                nextIter = 0;
                                intersection.set(nextPrevVertice[0], nextPrevVertice[1]);
                                nextPoint = intersection;
                            }

                        } else if (projDir >= 1) {
                            if (mLastPoint.x == nextPrevVertice[2] && mLastPoint.y == nextPrevVertice[3]) {
                                // proj on next vertices
                                vertIdx = getNextVertice(collisionPolyVertice, vertIdx, nextPrevVertice);
                                Gdx.app.debug("DEBUG", "projDir=" + projDir + " next vertice [" + nextPrevVertice[0] + "," + nextPrevVertice[1] + " - " + nextPrevVertice[2] + "," + nextPrevVertice[3] + "]");
                                nextIter--;
                            } else {
                                nextIter = 0;
                                intersection.set(nextPrevVertice[2], nextPrevVertice[3]);
                                nextPoint = intersection;
                            }
                        } else {
                            nextPoint = getProjectedPointOnLineFast(nextPrevVertice[0], nextPrevVertice[1], nextPrevVertice[2], nextPrevVertice[3], mCurrentPoint.x, mCurrentPoint.y);
                            nextIter = 0;
                        }
                    }
                  /*  if (nextPoint == null)
                        Gdx.app.debug("DEBUG", "NO NEXT POINT AFTER COLLISION");

                    else
                        Gdx.app.debug("DEBUG", "next point after collision [" + nextPoint.x + "," + nextPoint.y + "]");
*/
                }
            } else {
                nextPoint = mCurrentPoint;
            }

            if (nextPoint != null) {

                float distance = (float) Math.sqrt((nextPoint.x - mLastPoint.x) * (nextPoint.x - mLastPoint.x) + (nextPoint.y - mLastPoint.y) * (nextPoint.y - mLastPoint.y));
                if (distance > 0.1 && distance < 10) {
                    path.AddPoint(nextPoint.x, nextPoint.y, 0.1f);
                    mLastPoint.set(nextPoint);
                    mCurrentPoint.set(mLastPoint.x, mLastPoint.y);
                   // Gdx.app.debug("DEBUG", "add pointX=" + mLastPoint.x + " pointY=" + mLastPoint.y);
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
            mPosPoly.setPosition(bobBound.getX(), bobBound.getY());
            mCursorPoint.set(bobBound.getX(), bobBound.getY(), mCursorPoint.z);
            path.AddPoint(mCursorPoint.x, mCursorPoint.y, 0.01f);
            mLastPoint.set(mCursorPoint.x, mCursorPoint.y);
            mCurrentPoint.set(mCursorPoint.x, mCursorPoint.y);
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

    public boolean overlaps(Polygon polygon, Circle circle, Vector2 displacement) {
        float[] vertices = polygon.getTransformedVertices();
        Vector2 center = new Vector2(circle.x, circle.y);
        float squareRadius = circle.radius * circle.radius;
        Vector2 minDisplacement = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
        double minDist = minDisplacement.x * minDisplacement.x + minDisplacement.y * minDisplacement.y;
        double dist;
        boolean ovelaps = false;
        for (int i = 0; i < vertices.length; i += 2) {
            if (i == 0) {
                if (Intersector.intersectSegmentCircleDisplace(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius, displacement) != Float.POSITIVE_INFINITY) {
                    ovelaps = true;
                    dist = displacement.x * displacement.x + displacement.y * displacement.y;
                    if (dist < minDist) {
                        minDisplacement.set(displacement.x, displacement.y);
                        minDist = dist;
                    }
                }
            } else {
                if (Intersector.intersectSegmentCircleDisplace(new Vector2(vertices[i - 2], vertices[i - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius, displacement) != Float.POSITIVE_INFINITY) {
                    ovelaps = true;
                    dist = displacement.x * displacement.x + displacement.y * displacement.y;
                    if (dist < minDist) {
                        minDisplacement.set(displacement.x, displacement.y);
                        minDist = dist;
                    }
                }
            }
        }
        if (ovelaps) {
            displacement.set(minDisplacement.x, minDisplacement.y);
            return true;
        }
        return false;
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