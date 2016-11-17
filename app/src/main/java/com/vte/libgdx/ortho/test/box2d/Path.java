package com.vte.libgdx.ortho.test.box2d;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.PathComponent;

import java.util.ArrayList;

/**
 * Created by vincent on 20/07/2016.
 */

public class Path {
    ArrayList<Vector2> positions;
    ArrayList<Float> times;
    Vector2 velocity;
    int currentPointIndex;
    int nextPointIndex;
    int direction = 1;
    float lastTime = 0;
    static public final float CHECK_RADIUS = 0.2f;
    static public final float NEXT_POINT_DELAY = 0.3f;
    Entity entity;

    public Path() {
        positions = new ArrayList<Vector2>();
        times = new ArrayList<Float>();
        velocity = new Vector2();
        entity = new Entity();
        entity.add(new PathComponent(this));
        EntityEngine.getInstance().addEntity(entity);
    }

    public void AddPoint(float x, float y, float time) {
        positions.add(new Vector2(x, y));

        times.add(time);
    }

    public void Reset() {
        currentPointIndex = 0;
        nextPointIndex = GetNextPoint();
        SetNextPointVelocity();
    }

    public void destroy() {
        if (entity != null) {
            EntityEngine.getInstance().removeEntity(entity);
            entity = null;
        }
    }

    public Vector2 GetCurrentPoint() {
        return positions.get(currentPointIndex);
    }

    public boolean UpdatePath(Vector2 bodyPosition, float dT) {
        return ReachedNextPoint(bodyPosition, dT);
    }

    boolean ReachedNextPoint(Vector2 bodyPosition, float dT) {
        lastTime += dT;
        Vector2 nextPointPosition = positions.get(nextPointIndex);
        float d = nextPointPosition.dst2(bodyPosition);
        boolean hasReachedNextPoint = false;
        if (d < CHECK_RADIUS) {
            currentPointIndex = nextPointIndex;
            lastTime=0;
            if (hasNextPoint()) {
                nextPointIndex = GetNextPoint();
            }
            else
            {
                velocity.set(0,0);
            }
            hasReachedNextPoint = true;

        }
        computeVelocity(bodyPosition);
        return hasReachedNextPoint;
    }

    public boolean hasNextPoint() {
        int nextPoint = currentPointIndex + direction;
        if (nextPoint >= positions.size())
            return false;
        return true;
    }

    int GetNextPoint() {
        int nextPoint = currentPointIndex + direction;
        if (nextPoint == positions.size()) {
            nextPoint = 0;
        } else if (nextPoint == -1) {
            nextPoint = positions.size() - 1;
        }
        return nextPoint;
    }

    void SetNextPointVelocity() {
        Vector2 nextPosition = positions.get(nextPointIndex);
        Vector2 currentPosition = positions.get(currentPointIndex);
        float dx = nextPosition.x - currentPosition.x;
        float dy = nextPosition.y - currentPosition.y;
        float time = times.get(nextPointIndex);
        velocity.set(dx / time, dy / time);
    }
    void computeVelocity(Vector2 aCurrentPosition) {
        Vector2 nextPosition = positions.get(nextPointIndex);
         float dx = nextPosition.x - aCurrentPosition.x;
        float dy = nextPosition.y - aCurrentPosition.y;
        float time = NEXT_POINT_DELAY-lastTime;
       /* // constant speed
        float speed=1.5f;
float c = (dx*dx+dy*dy )/(speed*speed);
        velocity.set(dx / c, dy / c);*/
        velocity.set(dx / time, dy / time);
    }

    public Vector2 GetVelocity() {
        return velocity;
    }

    public void render(ShapeRenderer renderer) {
        for (int i = currentPointIndex; i < positions.size() - 1; i++) {
            Vector2 pointStart = positions.get(i);
            Vector2 pointEnd = positions.get(i + 1);
            renderer.line(pointStart.x, pointStart.y, 0, pointEnd.x, pointEnd.y, 0, Color.YELLOW, Color.YELLOW);
        }
    }
}
