package com.vte.libgdx.ortho.test.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.characters.Character;
import com.vte.libgdx.ortho.test.characters.CharacterDef;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;

/**
 * Created by vincent on 05/01/2017.
 */

public class MapInteractionNPJ extends Character implements IMapInteraction, IMapInteractionRendable {
    protected Type mType;
    protected boolean mIsRended = false;

    private TextureRegion mInteractionTextureRegion;
    private boolean mIsInteractionShown= false;

    public MapInteractionNPJ(float aX, float aY, CharacterDef aDef) {
        super(aDef);
        initialize();
        mType = Type.NPJ;
        setPosition(aX, aY);
        mInteractionTextureRegion = AssetsUtility.ITEMS_TEXTUREATLAS.findRegion("inv_shield");
        getPolygonShape().getShape().getTransformedVertices();
    }

    @Override
    public float getX() {
        return getPosition().x;
    }

    @Override
    public float getY() {
        return getPosition().y;
    }

    @Override
    public Type getInteractionType() {
        return null;
    }

    public boolean isRendable()
    {
        return true;
    }

    public boolean isRended()
    {
        return mIsRended;
    }

    public void setRended(boolean aRended)
    {
        mIsRended = aRended;
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        TransformComponent transform = this.getComponent(TransformComponent.class);

        if(mIsInteractionShown) {
            float width_frame = currentFrame.getRegionWidth();
            float height_frame = currentFrame.getRegionHeight();

            float width = mInteractionTextureRegion.getRegionWidth();
            float height = mInteractionTextureRegion.getRegionHeight();

            //Allow for Offset
            float originX = (width_frame-width)/2*transform.scale;//transform.originOffset.x;
            float originY = height_frame*transform.scale;//transform.originOffset.y;

            batch.draw(mInteractionTextureRegion,
                    transform.position.x + transform.originOffset.x, transform.position.y + transform.originOffset.y,
                    originX, originY,
                    width, height,
                    transform.scale, transform.scale,
                    transform.angle);
        }
    }

    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        boolean ret = super.onCollisionStart(aEntity);
        if(ret && aEntity.mType == CollisionComponent.Type.CHARACTER) {
            Gdx.app.debug("DEBUG", "NPJ collision start");
            mIsInteractionShown = true;
        }
        return ret;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        boolean ret = super.onCollisionStop(aEntity);
        if(ret && aEntity.mType == CollisionComponent.Type.CHARACTER) {
            Gdx.app.debug("DEBUG", "NPJ collision stop");
            mIsInteractionShown = false;
        }
        return ret;
    }
}
