package com.vte.libgdx.ortho.test.entity;

import com.badlogic.ashley.core.Engine;

/**
 * Created by vincent on 07/07/2016.
 */

public class EntityEngine {

    static private Engine s_engine = new Engine();

    public static Engine getInstance() {
        return s_engine;
    }

}
