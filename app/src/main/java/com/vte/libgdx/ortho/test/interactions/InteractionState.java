package com.vte.libgdx.ortho.test.interactions;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public String name;
    public int fps;
    public ArrayList<String> frames;

    private Animation mAnimation;

    public void init(TextureAtlas aAtlas)
    {
        if(frames==null && frames.size()<=0)
            return;

        Array<TextureRegion> regions = new Array<TextureRegion>();
        for(String key:frames)
        {
            TextureRegion reg = aAtlas.findRegion(key);
            if(reg!=null)
            {
                regions.add( reg);
            }
        }
        if(fps==0) {
            fps = 1;
        }
        mAnimation = new Animation((float) (1/fps), regions);
        mAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public TextureRegion getTextureRegion(float aTime)
    {
        return mAnimation.getKeyFrame(aTime, true);
    }
}
