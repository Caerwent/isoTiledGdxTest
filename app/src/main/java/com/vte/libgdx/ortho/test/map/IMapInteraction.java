package com.vte.libgdx.ortho.test.map;

/**
 * Created by gwalarn on 13/11/16.
 */

public interface IMapInteraction {
    public enum Type {
        ITEM,
        CHESS,
        NPJ,
        PORTAL,
        MONSTER,
        PATH
    }


    public float getX();

    public float getY();

    public Type getInteractionType();

    public String getQuestId();

    public void setQuestId(String aQuestId);

}
