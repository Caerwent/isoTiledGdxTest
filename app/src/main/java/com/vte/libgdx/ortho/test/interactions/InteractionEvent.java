package com.vte.libgdx.ortho.test.interactions;

/**
 * Created by vincent on 13/02/2017.
 */

public class InteractionEvent {
    public static enum EventType {
        STATE;
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
 }
