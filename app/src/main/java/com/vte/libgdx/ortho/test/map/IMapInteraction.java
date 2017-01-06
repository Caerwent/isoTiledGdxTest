package com.vte.libgdx.ortho.test.map;

/**
 * Created by gwalarn on 13/11/16.
 */

public interface IMapInteraction {
    public enum Type {
        START,
        ITEM,
        CHESS,
        NPJ
    }



    public float getX();
    public float getY();

    public Type getInteractionType();

}
