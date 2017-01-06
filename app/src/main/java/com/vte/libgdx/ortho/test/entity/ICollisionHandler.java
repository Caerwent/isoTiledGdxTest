package com.vte.libgdx.ortho.test.entity;

import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;

/**
 * Created by vincent on 26/07/2016.
 */

public interface ICollisionHandler {
    public boolean onCollisionStart(CollisionComponent aEntity);

    public boolean onCollisionStop(CollisionComponent aEntity);

    public Array<CollisionComponent> getCollisions();
}
