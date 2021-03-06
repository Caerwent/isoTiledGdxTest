package com.vte.libgdx.ortho.test.interactions;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by vincent on 08/02/2017.
 */

public class InteractionState {

    public final static String STATE_MOVE_LEFT="MOVE_LEFT";
    public final static String STATE_MOVE_RIGHT="MOVE_RIGHT";
    public final static String STATE_MOVE_DOWN="MOVE_DOWN";
    public final static String STATE_MOVE_UP="MOVE_UP";
    public final static String STATE_FROZEN="FROZEN";
    public final static String STATE_BURNING="BURNING";
    public String name;
    public boolean isLooping;
    public int fps;
    public ArrayList<String> frames;

    private Animation mAnimation;
    private TextureAtlas.AtlasRegion mFixRegion;

    public void init(TextureAtlas aAtlas)
    {
        if(frames==null || frames.size()<=0)
            return;

        if(fps==0 || frames.size()==1) {
            mFixRegion=aAtlas.findRegion(frames.get(0));
        }
        else {
            Array<TextureAtlas.AtlasRegion> regions = new Array<TextureAtlas.AtlasRegion>();
            for (String key : frames) {
                TextureAtlas.AtlasRegion reg = aAtlas.findRegion(key);
                if (reg != null) {
                    regions.add(reg);
                }
            }

            mAnimation = new Animation((1F / (float)fps), regions);
            mAnimation.setPlayMode(isLooping ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        }
    }

    public TextureAtlas.AtlasRegion getTextureRegion(float aTime)
    {
        if(mFixRegion!=null)
        {
            return mFixRegion;
        }
        else if(mAnimation!=null) {
            return (TextureAtlas.AtlasRegion) mAnimation.getKeyFrame(aTime, true);
        }
        return null;
    }

    public boolean isCompleted(float stateTime)
    {
        if(mFixRegion!=null && isLooping)
        {
            return false;
        }
        else if(mAnimation!=null)
        {
            return mAnimation.isAnimationFinished(stateTime);
        }
        return true;
    }
}
