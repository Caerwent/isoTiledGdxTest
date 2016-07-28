package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.box2d.Path;

/**
 * Created by vincent on 26/07/2016.
 */

public class PathComponent implements Component {
    public Path mPath;


    public PathComponent(Path aPath) {
        mPath = aPath;
    }
}
