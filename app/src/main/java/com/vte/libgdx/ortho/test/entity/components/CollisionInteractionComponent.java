package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.interactions.IInteraction;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionInteractionComponent implements Component {



    public Shape mShape;
    public ICollisionInteractionHandler mHandler;
    public IInteraction mInteraction;


    public CollisionInteractionComponent() {
    }

    public CollisionInteractionComponent(Shape shape, IInteraction aInteraction, ICollisionInteractionHandler aHandler) {
        mShape = shape;
        mHandler = aHandler;
        mInteraction=aInteraction;
    }
}
