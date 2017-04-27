package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

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
        float squareRadius = circle.radius * circle.radius;
        boolean ovelaps = false;


        if(Intersector.isPointInPolygon(vertices, 0, vertices.length, center.x, center.y))
        {
            ovelaps = true;
        }
        else {
            for (int i = 0; i < vertices.length; i += 2) {
                if (i == 0) {
                    if (Intersector.intersectSegmentCircle(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius)) {
                        ovelaps = true;
                    }
                } else {
                    if (Intersector.intersectSegmentCircle(new Vector2(vertices[i - 2], vertices[i - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius)) {
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

        if(intersectPolygons(polygon, rectPolygon))
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
            Rectangle rect = aShape.getBounds();
            logStr="("+rect.x+", "+rect.y+") ("+(rect.x+rect.getWidth())+", "+rect.y+") ("+(rect.x+rect.getWidth())+", "+(rect.y+rect.getHeight())+") ("+rect.x+", "+(rect.y+rect.getHeight())+")";
        }
        return logStr;
    }
    /** Returns true if the specified point is in the polygon.
     * @param offset Starting polygon index.
     * @param count Number of array indices to use after offset. */
    public static boolean isPointInPolygon (float[] polygon, int offset, int count, float x, float y) {
        boolean oddNodes = false;
        int j = offset + count - 2;
        for (int i = offset, n = j; i <= n; i += 2) {
            float yi = polygon[i + 1];
            float yj = polygon[j + 1];
            if ((yi < y && yj > y) || (yj < y && yi > y)) {
                float xi = polygon[i];
                if (xi + (y - yi) / (yj - yi) * (polygon[j] - xi) < x) oddNodes = !oddNodes;
            }
            j = i;
        }
        return oddNodes;
    }
    /** Intersects the two closed polygons and returns the polygon resulting from the intersection.
     *  Follows the Sutherland-Hodgman algorithm.
     *
     * @param p1 The polygon that is being clipped
     * @param p2 The clip polygon
     * @return Whether the two polygons intersect.
     */
    public static boolean intersectPolygons (Polygon p1, Polygon p2) {
        //Convert polygons into more practical format
        ArrayList<Vector2> p1Points = new ArrayList<Vector2>();
        ArrayList<Vector2> p2Points = new ArrayList<Vector2>();
        float[] vertices1, vertices2;
        vertices1 = p1.getTransformedVertices();
        vertices2 = p2.getTransformedVertices();

        for (int i = 0; i < vertices1.length; i += 2) {
            if(isPointInPolygon(vertices2,0,vertices2.length,vertices1[i], vertices1[i + 1] )) {
                return true;
            }
        }
        for (int i = 0; i < vertices2.length; i += 2) {
            if(isPointInPolygon(vertices1,0,vertices1.length,vertices2[i], vertices2[i + 1] )) {
                return true;
            }
        }

        return false;

    }

    static public boolean overlaps(Shape aShape, Shape aOtherShape)
    {

        if(aShape.getType()==Shape.Type.POLYGON && aOtherShape.getType()==Shape.Type.POLYGON) {
            if (intersectPolygons(((PolygonShape) aShape).getShape(), ((PolygonShape) aOtherShape).getShape())) {
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
