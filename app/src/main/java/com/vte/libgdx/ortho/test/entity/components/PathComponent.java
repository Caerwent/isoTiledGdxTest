package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.box2d.PathHero;

/**
 * Created by vincent on 26/07/2016.
 */

public class PathComponent implements Component {
    public PathHero mPath;


    public PathComponent(PathHero aPath) {
        mPath = aPath;
    }
}
