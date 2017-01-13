package com.vte.libgdx.ortho.test.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.characters.CharacterDef;
import com.vte.libgdx.ortho.test.characters.CharacterNPJ;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.InputComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.screens.ScreenManager;

/**
 * Created by vincent on 05/01/2017.
 */

public class MapInteractionNPJ extends CharacterNPJ implements IMapInteraction, IMapInteractionRendable, InputProcessor {

    protected Type mType;
    protected boolean mIsRended = false;
    protected String mQuestId;

    private TextureRegion mInteractionTextureRegion;
    private boolean mIsInteractionShown = false;
    private RectangleShape mMarkShape;

    public MapInteractionNPJ(float aX, float aY, CharacterDef aDef) {
        super(aDef);
        mInteractionTextureRegion = AssetsUtility.ITEMS_TEXTUREATLAS.findRegion("inv_shield");

        initialize();
        mType = Type.NPJ;
        setPosition(aX, aY);
        getPolygonShape().getShape().getTransformedVertices();
        updateInteractionMarkShape();

    }

    @Override
    public void initialize() {
        super.initialize();
        mMarkShape= new RectangleShape();
        add(new InputComponent(this));
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
        return mType;
    }

    @Override
    public String getQuestId()
    {
        return mQuestId;
    }

    @Override
    public void setQuestId(String aQuestId)
    {
        mQuestId = aQuestId;
    }
    public boolean isRendable() {
        return true;
    }

    public boolean isRended() {
        return mIsRended;
    }

    public void setRended(boolean aRended) {
        mIsRended = aRended;
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        TransformComponent transform = this.getComponent(TransformComponent.class);

        if (mIsInteractionShown) {
            float width_frame = currentFrame.getRegionWidth();
            float height_frame = currentFrame.getRegionHeight();

            float width = mInteractionTextureRegion.getRegionWidth();
            float height = mInteractionTextureRegion.getRegionHeight();

            //Allow for Offset
            float originX = (width_frame - width) / 2 * transform.scale;//transform.originOffset.x;
            float originY = height_frame * transform.scale;//transform.originOffset.y;

            batch.draw(mInteractionTextureRegion,
                    transform.position.x + transform.originOffset.x, transform.position.y + transform.originOffset.y,
                    originX, originY,
                    width, height,
                    transform.scale, transform.scale,
                    transform.angle);
        }
    }
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateInteractionMarkShape();

    }

    public void setPosition(Vector2 pos) {
        super.setPosition(pos);
        updateInteractionMarkShape();
    }

    public void updateInteractionMarkShape() {
        if(mMarkShape==null)
            return;

        TransformComponent transform = this.getComponent(TransformComponent.class);
        float width_frame = currentFrame.getRegionWidth();
        float height_frame = currentFrame.getRegionHeight();

        float width = mInteractionTextureRegion.getRegionWidth();
        float height = mInteractionTextureRegion.getRegionHeight();

        //Allow for Offset
        float originX = (width_frame - width) / 2 * transform.scale;//transform.originOffset.x;
        float originY = height_frame * transform.scale;//transform.originOffset.y;

        mMarkShape.getShape().set(0, 0, width, height);
        mMarkShape.setX(transform.position.x + transform.originOffset.x + originX);
        mMarkShape.setY(transform.position.y + transform.originOffset.y + originY);

    }

    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        boolean ret = super.onCollisionStart(aEntity);
        if (ret && aEntity.mType == CollisionComponent.Type.CHARACTER) {
            Gdx.app.debug("DEBUG", "NPJ collision start");
            mIsInteractionShown = true;
        }
        return ret;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        boolean ret = super.onCollisionStop(aEntity);
        if (ret && aEntity.mType == CollisionComponent.Type.CHARACTER) {
            Gdx.app.debug("DEBUG", "NPJ collision stop");
            mIsInteractionShown = false;
            onInteractionStop();
        }
        return ret;
    }

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 cursorPoint = new Vector3();

        ScreenManager.getInstance().getScreen().getCamera().unproject(cursorPoint.set(screenX, screenY, 0));

        if (mIsInteractionShown && mMarkShape.getBounds().contains(cursorPoint.x, cursorPoint.y)) {
           onInteractionStart();
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
