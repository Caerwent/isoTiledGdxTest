package com.vte.libgdx.ortho.test.map;

/**
 * Created by gwalarn on 13/11/16.
 */

public interface IItemInteraction {
    public enum Type {
        ITEM
    }


    public float getX();

    public float getY();

    public Type getInteractionType();

}
