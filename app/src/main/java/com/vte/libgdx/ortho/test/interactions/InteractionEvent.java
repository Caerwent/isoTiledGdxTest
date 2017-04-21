package com.vte.libgdx.ortho.test.interactions;

/**
 * Created by vincent on 13/02/2017.
 */

public class InteractionEvent {
    public static final String THIS="THIS";

    public static enum EventType {
        STATE,
        END_STATE,
        DIALOG,
        EFFECT_START,
        EFFECT_STOP;
    }
    public String sourceId;
    public String type;
    public String value;

    private boolean mIsPerformed=false;

    public boolean isPerformed()
    {
        return mIsPerformed;
    }

    public void setPerformed(boolean isPerformed)
    {
        mIsPerformed=isPerformed;
    }

    public InteractionEvent(){}

    public InteractionEvent(String aSourceId, String aType, String aValue)
    {
        sourceId = aSourceId;
        type=aType;
        value=aValue;
    }
 }
