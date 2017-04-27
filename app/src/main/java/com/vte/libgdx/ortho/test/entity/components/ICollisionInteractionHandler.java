package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.gdx.utils.Array;

/**
 * Created by vincent on 26/07/2016.
 */

public interface ICollisionInteractionHandler {
    public boolean onCollisionInteractionStart(CollisionInteractionComponent aEntity);

    public boolean onCollisionInteractionStop(CollisionInteractionComponent aEntity);

    public Array<CollisionInteractionComponent> getCollisionInteraction();

    public boolean onCollisionEffectStart(CollisionEffectComponent aEntity);

    public boolean onCollisionEffectStop(CollisionEffectComponent aEntity);

    public Array<CollisionEffectComponent> getCollisionEffect();
}
