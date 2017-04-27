package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.effects.Effect;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionEffectComponent implements Component {



    public Effect mEffect;
    public Shape mZoneEffect;
    public String mSourceId;


    public CollisionEffectComponent() {
    }

    public CollisionEffectComponent(Effect aEffect, Shape aZoneEffect, String aSourceId) {
        mEffect = aEffect;
        mZoneEffect = aZoneEffect;
        mSourceId = aSourceId;
    }
}
