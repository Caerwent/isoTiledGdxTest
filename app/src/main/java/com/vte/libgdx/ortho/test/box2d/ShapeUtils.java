package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by gwalarn on 20/11/16.
 */

public class ShapeUtils {
    /**
     * Get projected point of P on line v1,v2. Faster version. (http://www.sunshine2k.de/coding/java/PointOnLine/PointOnLine.html)
     *
     * @return projected point p.
     */
    static public Vector2 getProjectedPointOnLineFast(float vx1, float vy1, float vx2, float vy2, float px, float py) {
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
    static public int isProjectedPointOnLineSegment(float vx1, float vy1, float vx2, float vy2, float px, float py) {
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

    static public double dotProduct(Vector2 e1, Vector2 e2) {
        return e1.x * e2.x + e1.y * e2.y;
    }

    static public int getNextVertice(float[] vertices, int currIdx, float[] nextVertices) {
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

    static public int getPreviousVertice(float[] vertices, int currIdx, float[] nextVertices) {
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

    static public boolean overlaps(Polygon polygon, Circle circle) {
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
    static public boolean overlaps(Polygon polygon, Rectangle rect) {
        Polygon rectPolygon = new Polygon(new float[]{0, 0, rect.getWidth(), 0, rect.getWidth(), rect.getHeight(), 0, rect.getHeight()});
        rectPolygon.setPosition(rect.x , rect.y );
        boolean ovelaps = false;


        if(Intersector.overlapConvexPolygons(polygon, rectPolygon, null))
        {
            ovelaps = true;
        }
        return ovelaps;
    }
    static public String logShape(Shape aShape) {
        if(aShape==null)
            return "null";
        String logStr="";
        if(aShape.getType()== Shape.Type.POLYGON)
        {
            float[] vertices = ((PolygonShape) aShape).getShape().getTransformedVertices();
            logStr = "[";
            for (int i = 0; i <= vertices.length - 2; i += 2) {
                logStr += "(" + vertices[i] + "," + vertices[i + 1] + ") ";
            }
            logStr += "]";
        }
        else if(aShape.getType()== Shape.Type.CIRCLE)
        {
            logStr="(X="+((CircleShape) aShape).getX()+", Y="+((CircleShape) aShape).getY()+", R="+((CircleShape) aShape).getShape().radius+")";
        }
        else if(aShape.getType()== Shape.Type.RECT)
        {
            logStr="(X="+((RectangleShape) aShape).getX()+", Y="+((RectangleShape) aShape).getY()+", WxH="+((RectangleShape) aShape).getShape().getWidth()+"x"+((RectangleShape) aShape).getShape().getHeight()+")";
        }
        return logStr;
    }

    static public boolean overlaps(Shape aShape, Shape aOtherShape)
    {
        if(aShape.getType()==Shape.Type.POLYGON && aOtherShape.getType()==Shape.Type.POLYGON) {
            if (Intersector.overlapConvexPolygons(((PolygonShape) aShape).getShape(), ((PolygonShape) aOtherShape).getShape(), null)) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.POLYGON && aOtherShape.getType()==Shape.Type.CIRCLE) {
            if (ShapeUtils.overlaps(((PolygonShape) aShape).getShape(), ((CircleShape) aOtherShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.POLYGON && aOtherShape.getType()==Shape.Type.RECT) {
            if (ShapeUtils.overlaps(((PolygonShape) aShape).getShape(), ((RectangleShape) aOtherShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.CIRCLE && aOtherShape.getType()==Shape.Type.POLYGON) {
            if (ShapeUtils.overlaps(((PolygonShape) aOtherShape).getShape(), ((CircleShape) aShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.CIRCLE && aOtherShape.getType()==Shape.Type.CIRCLE) {
            if (Intersector.overlaps(((CircleShape) aShape).getShape(), ((CircleShape) aOtherShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.CIRCLE && aOtherShape.getType()==Shape.Type.RECT) {
            if (Intersector.overlaps(((CircleShape) aShape).getShape(), ((RectangleShape) aOtherShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.RECT && aOtherShape.getType()==Shape.Type.POLYGON) {
            if (ShapeUtils.overlaps(((PolygonShape) aOtherShape).getShape(), ((RectangleShape) aShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.RECT && aOtherShape.getType()==Shape.Type.CIRCLE) {
            if (Intersector.overlaps(((CircleShape) aOtherShape).getShape(), ((RectangleShape) aShape).getShape())) {
                return true;
            }
        }
        else if(aShape.getType()==Shape.Type.RECT && aOtherShape.getType()==Shape.Type.RECT) {
            if (Intersector.overlaps(((RectangleShape) aShape).getShape(), ((RectangleShape) aOtherShape).getShape())) {
                return true;
            }
        }
        return false;
    }
}
