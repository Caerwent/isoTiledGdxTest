package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.gdx.utils.Array;

/**
 * Created by vincent on 26/07/2016.
 */

public interface ICollisionObstacleHandler {
    public boolean onCollisionObstacleStart(CollisionObstacleComponent aEntity);

    public boolean onCollisionObstacleStop(CollisionObstacleComponent aEntity);

    public Array<CollisionObstacleComponent> getCollisionObstacle();
}
