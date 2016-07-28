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
    static final float CHECK_RADIUS = 1f;
    Entity entity;

    public Path() {
        positions = new ArrayList<Vector2>();
        times = new ArrayList<Float>();
        velocity = new Vector2();
        entity = new Entity();
        entity.add(new PathComponent(this));
        EntityEngine.getInstance().addEntity(entity);
    }

    public void AddPoint(Vector2 pos, float time) {
        positions.add(pos);

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

        if (d < CHECK_RADIUS) {
            currentPointIndex = nextPointIndex;
            if (hasNextPoint()) {
                nextPointIndex = GetNextPoint();
                SetNextPointVelocity();
            }
            return true;

        }
        return false;
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
