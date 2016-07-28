package com.vte.libgdx.ortho.test.entity;

import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;

/**
 * Created by vincent on 26/07/2016.
 */

public interface ICollisionHandler {
    public void onCollisionStart(CollisionComponent aEntity);

    public void onCollisionStop(CollisionComponent aEntity);

    public Array<CollisionComponent> getCollisions();
}
