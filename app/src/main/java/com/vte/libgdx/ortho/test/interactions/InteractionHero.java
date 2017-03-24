package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.box2d.PathHero;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.effects.EffectFactory;
import com.vte.libgdx.ortho.test.entity.components.BobComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.map.ItemInteraction;
import com.vte.libgdx.ortho.test.map.MapTownPortalInfo;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionHero extends Interaction {

    protected float stateTime; // elapsed time
    MapTownPortalInfo mPortalInfo;

    public InteractionHero(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.HERO;
        CollisionComponent collisionComponent = this.getComponent(CollisionComponent.class);
        collisionComponent.mType = CollisionComponent.CHARACTER;
        this.add(new BobComponent(this));

    }

    PathHero mPath;
    @Override
    public void setMovable(boolean isMovable) {
        super.setMovable(isMovable);
        if(mPath!=null && !isMovable())
        {
            setPath(null);
        }
    }
    public void setPath(PathHero p) {
        mPath = p;
        if (mPath == null) {
            setVelocity(0, 0);
        } else {
            mPath.reset();
        }

        stateTime = 0;
    }


    public void renderShadowed(float x, float y, Batch batch) {
        TransformComponent transform = this.getComponent(TransformComponent.class);

        float width = mCurrentFrame.getRegionWidth();
        float height = mCurrentFrame.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;
        Color color = batch.getColor();
        float oldTrans = color.a;
        color.a *= 0.5;

        batch.draw(mCurrentFrame,
                x + transform.originOffset.x, y + transform.originOffset.y,
                originX, originY,
                width, height,
                transform.scale, transform.scale,
                transform.angle);
        color.a = oldTrans;
    }

    public void update(float dt) {
        super.update(dt);
        if (mPath != null) {
            if (mPath.hasNextPoint()) {
                TransformComponent transform = this.getComponent(TransformComponent.class);

                Vector2 pos2D = new Vector2(transform.position.x, transform.position.y);
                Vector2 velocity = mPath.getVelocityForPosAndTime(pos2D, dt);
                setVelocity(velocity);

                stateTime += dt;

            } else {
                mPath.destroy();
                mPath = null;
                setVelocity(0, 0);
                stateTime = 0;
            }

        } else {
            setVelocity(0, 0);
            stateTime = 0;
        }

    }

    @Override
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        if ((aEntity.mType & CollisionComponent.ITEM) != 0) {
            AudioManager.getInstance().onAudioEvent(AudioManager.ITEM_FOUND_SOUND);

            EventDispatcher.getInstance().onItemFound((((ItemInteraction) aEntity.mData).getItem()));
            return false;
        }
        if ( ( aEntity.mType & CollisionComponent.OBSTACLE) != 0 || ( (aEntity.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) != 0)) {

            if ( ( (aEntity.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) == 0 &&
                    getShape().getBounds().getY() >= aEntity.mShape.getBounds().getY()) ||

                    (getShape().getBounds().getY() >= aEntity.mShape.getBounds().getY() &&
                            (aEntity.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) != 0 &&
                            (getShape().getBounds().getY() - aEntity.mShape.getBounds().getY() <= 0.5))
                    )
            {

                return true;


            }
        }
        return false;
    }

    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {
        if ( (((aEntity.mType & CollisionComponent.OBSTACLE) != 0) || ((aEntity.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) != 0) )
            && mPath != null) {
            mPath.destroy();
            mPath = null;
            setVelocity(0, 0);

        }
    }

    @Override
    protected void stopLaunchedEffect() {
        Effect stoppedEffect = mEffectLaunched;
        super.stopLaunchedEffect();

        if(stoppedEffect!=null && stoppedEffect.id== Effect.Type.PORTAL)
        {
            // check if it is :
            // - an arrival into the default map
            // - a come back to the invoking map
            // - an invocation into a map
            // - an invocation into the default map
            if(mPortalInfo!=null)
            {
                if(mPortalInfo.originMap.compareTo(mMap.getMapName())==0)
                {
                    // it's a come back to the invoking map
                    mPortalInfo = null;
                    stoppedEffect.setPlayMode(Animation.PlayMode.NORMAL);
                }
                else if(stoppedEffect.getPlayMode()== Animation.PlayMode.REVERSED) {
                    // it's an arrival into the default map
                    stoppedEffect.setPlayMode(Animation.PlayMode.NORMAL);
                }
                else
                {
                    // it's an invocation into the default map
                    EventDispatcher.getInstance().onNewMapRequested(mPortalInfo.originMap, mPortalInfo);
                }
            }
            else {
                // it's an invocation into a map
                mPortalInfo = new MapTownPortalInfo();
                mPortalInfo.originMap = mMap.getMapName();
                mPortalInfo.x = getX();
                mPortalInfo.y = getY();

                EventDispatcher.getInstance().onNewMapRequested(MyGame.DEFAULT_MAP_NAME, mPortalInfo);
            }
        }

    }

    public void launchTownPortalArrivalEffect(MapTownPortalInfo aPortalInfo)
    {
        mPortalInfo = aPortalInfo;

        Effect portalBackEffect = EffectFactory.getInstance().getEffect(Effect.Type.PORTAL);
        portalBackEffect.setPlayMode(Animation.PlayMode.REVERSED);
        launchEffect(portalBackEffect);
    }


}
