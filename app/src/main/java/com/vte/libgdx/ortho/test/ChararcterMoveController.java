package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
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
    final Vector3 curr = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();

    Vector2 mLastPoint = new Vector2();

    Bob mBob;
    Path path;
    public boolean isActive = false;

    public ChararcterMoveController(OrthographicCamera camera, Bob aBob) {
        this.camera = camera;
        mBob = aBob;

    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (!isActive)
            return false;
        camera.unproject(curr.set(x, y, 0));

        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
            camera.unproject(delta.set(last.x, last.y, 0));
            delta.sub(curr);
            Vector2 nextPoint = null;
            Vector2 cursorPoint = new Vector2(curr.x, curr.y);
            Vector2 intersection = new Vector2();
            float[] nearestVertice = new float[4];
            int nearestVerticeOffset=0;

            Array<Polygon> collisions = MapBodyManager.getInstance().getBodiesCollision();
            if (collisions != null) {
                float[] vertices;
                for (Polygon poly : collisions) {
                    vertices = poly.getTransformedVertices();

                    // search for the nearest polygon segment on last point and touch point segment
                    if (!Intersector.intersectSegmentPolygon(mLastPoint, cursorPoint, poly) && !Intersector.isPointInPolygon(vertices, 0, vertices.length, curr.x, curr.y)) {
                        nextPoint = cursorPoint;
                    } else {
                        float nearestDist = Float.MAX_VALUE;

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
                            float dist = Intersector.distanceSegmentPoint(vertices[idx1], vertices[idx2], vertices[idx3], vertices[idx4],
                                    mLastPoint.x, mLastPoint.y);
                            if (dist < nearestDist) {
                                nearestDist = dist;
                                nearestVerticeOffset = idx1;
                                nearestVertice[0] = vertices[idx1];
                                nearestVertice[1] = vertices[idx2];
                                nearestVertice[2] = vertices[idx3];
                                nearestVertice[3] = vertices[idx4];
                            }
                        }
                    }
                }
                
                if (Intersector.intersectSegments(nearestVertice[0], nearestVertice[1], nearestVertice[2], nearestVertice[3],
                        mLastPoint.x, mLastPoint.y, curr.x, curr.y,
                        intersection)) {
                    Vector2 tmpNextPoint = intersection;

                    float distance = (float) Math.sqrt((intersection.x - mLastPoint.x) * (intersection.x - mLastPoint.x) + (intersection.y - mLastPoint.y) * (intersection.y - mLastPoint.y));
                    Gdx.app.debug("DEBUG", "intersection dist=" + distance + " [" + nearestVertice[0] + "," + nearestVertice[1] + "-" + nearestVertice[2] + "," + nearestVertice[3] + "] [" +
                            +mLastPoint.x + "," + mLastPoint.y + "-" + curr.x + "," + curr.y + "] => [" + intersection.x + "," + intersection.y + "]");

                    if (distance < 0.5) {
                        tmpNextPoint = getProjectedPointOnLineFast(nearestVertice[0], nearestVertice[1], nearestVertice[2], nearestVertice[3], curr.x, curr.y);
                    }

                    nextPoint = tmpNextPoint;

                }

            } else {
                nextPoint = cursorPoint;
            }

            if (nextPoint != null) {

                float distance = (float) Math.sqrt((nextPoint.x - mLastPoint.x) * (nextPoint.x - mLastPoint.x) + (nextPoint.y - mLastPoint.y) * (nextPoint.y - mLastPoint.y));
                Gdx.app.debug("DEBUG", "NEXT POINT dist=" + distance + " X=" + nextPoint.x + " Y=" + nextPoint.y);
                if (distance > 0.1) {
                    Gdx.app.debug("DEBUG", "add pointX=" + nextPoint.x + " pointY=" + nextPoint.y);
                    path.AddPoint(nextPoint, 0.1f);
                    mLastPoint.set(nextPoint);
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

        final Vector3 curr = new Vector3();
        camera.unproject(curr.set(x, y, 0));

        if (mBob.getBound().contains(curr.x, curr.y)) {
            if (path != null)
                path.destroy();
            path = new Path();

            Gdx.app.debug("DEBUG", "add pointX=" + curr.x + " pointY=" + curr.y);
            Vector2 point2d = new Vector2(curr.x, curr.y);
            path.AddPoint(point2d, 0.01f);
            mLastPoint.set(point2d);
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
        //e1 = (v2, v1) and e2 = (p, v1)
        //If p' matches with v1 (this means both have the same location), then
        //DP(e1, e2) = 0. e1 is then perpendicular to e2.
        //On the other side, if p' matches with v2 (thus p' is the same as v2), then
        //DP(e1, e2) = DP(e1, e1) = e1.x^2 + e1.y^2.
        //If p' lies somewhere between v1 and v2, then for the dot-product of e1 and e2 applies:
        //0 <= DP(e1,e2) <= e1.x^2 + e1.y^2.

        Vector2 e1 = new Vector2(vx2 - vx1, vy2 - vy1);
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
}