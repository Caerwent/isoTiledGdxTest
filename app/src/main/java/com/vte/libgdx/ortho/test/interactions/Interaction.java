package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.InputComponent;
import com.vte.libgdx.ortho.test.entity.components.InteractionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IInteractionEventListener;
import com.vte.libgdx.ortho.test.map.GameMap;

import java.util.ArrayList;

/**
 * Created by vincent on 08/02/2017.
 */

public class Interaction extends Entity implements ICollisionHandler, IInteraction, InputProcessor, IInteractionEventListener {

    protected String mId;
    protected Type mType;
    protected InteractionDef mDef;
    protected boolean mIsMovable;

    protected float mStateTime; // elapsed time
    protected boolean mIsRended = false;
    protected TextureRegion mCurrentFrame; // current animation frame
    protected TextureAtlas mAtlas;

    protected InteractionState mCurrentState;

    protected Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();
    protected Shape mShape;
    protected float[] mVertices = new float[8];


    protected Camera mCamera;

    public ArrayList<InteractionEventAction> mEventsAction;
    public ArrayList<InteractionEvent> mOutputEvents;

    protected MapProperties mProperties;
    protected GameMap mMap;

    @Override
    public void setCamera(Camera aCamera) {
        mCamera = aCamera;
    }

    public Interaction(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        mId = aMapping.id;
        mDef = aDef;
        mEventsAction = aMapping.eventsAction;
        mOutputEvents = aMapping.outputEvents;
        mMap = aMap;
        mProperties = aProperties;
    //    mType = IInteraction.Type.valueOf(mDef.type);
        if (mOutputEvents != null) {
            for (InteractionEvent event : mOutputEvents) {
                event.sourceId = mId;
            }
        }
        mShape = createShape();
        initialize(x, y);
        if (mEventsAction != null) {
            EventDispatcher.getInstance().addInteractionEventListener(this);
        }

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
    public Type getType() {
        return mType;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public boolean isClickable() {
        return mDef.isClickable;
    }

    @Override
    public boolean isMovable() {
        return mIsMovable;
    }

    public void setMovable(boolean isMovable) {
        mIsMovable = isMovable;
        if (mIsMovable) {
            this.add(new VelocityComponent());
        } else {
            remove(VelocityComponent.class);
        }
    }

    @Override
    public boolean isPersistent() {
        return mDef.isPersistent;
    }

    protected InteractionState getState(String aStateName) {
        for (InteractionState state : mDef.states) {
            if (state.name.compareTo(aStateName) == 0) {
                return state;
            }
        }
        return null;
    }

    protected void setState(String aStateName) {
        InteractionState state = getState(aStateName);
        if (state != null) {
            mCurrentState = state;
            if (mOutputEvents != null) {
                for (InteractionEvent event : mOutputEvents) {
                    if (InteractionEvent.EventType.STATE == InteractionEvent.EventType.valueOf(event.type)) {
                        EventDispatcher.getInstance().onInteractionEvent(event);
                    }
                }
            }
        }
    }

    public void destroy() {
        EventDispatcher.getInstance().removeInteractionEventListener(this);
    }

    public void initialize(float x, float y) {

        EntityEngine.getInstance().addEntity(this);

        add(new InteractionComponent(this));

        this.add(new TransformComponent());
        setMovable(mDef.isMovable);

        mCurrentState = getState(mDef.defaultState);

        if (mDef.isRendable) {
            mAtlas = new TextureAtlas("data/interactions/" + mDef.atlas);
            for (InteractionState state : mDef.states) {
                state.init(mAtlas);
            }
            mCurrentFrame = mCurrentState.getTextureRegion(0F);
            if (mCurrentFrame != null) {
                TransformComponent transform = this.getComponent(TransformComponent.class);
                transform.scale = MyGame.SCALE_FACTOR;

                transform.setOriginOffset(-mCurrentFrame.getRegionWidth() * transform.scale / 2, -mCurrentFrame.getRegionHeight() * transform.scale / 2);
                this.add(new VisualComponent(mCurrentFrame, this));
            }

        }
        setPosition(x, y);
        this.add(new CollisionComponent((byte) (CollisionComponent.MAPINTERACTION | CollisionComponent.OBSTACLE), getShape(), mId, this, this));


    }

    public void setPosition(float x, float y) {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        transformComponent.position.x = x;
        transformComponent.position.y = y;

    }

    public void setPosition(Vector2 pos) {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        transformComponent.position.x = pos.x;
        transformComponent.position.y = pos.y;

    }

    public Vector2 getPosition() {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        return new Vector2(transformComponent.position.x, transformComponent.position.y);
    }

    public void setVelocity(float vx, float vy) {
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        if (velocity != null) {
            velocity.x = vx;
            velocity.y = vy;
        }
    }

    public void setVelocity(Vector2 v) {
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        if (velocity != null) {
            velocity.x = v.x;
            velocity.y = v.y;
        }
    }

    public boolean isRendable() {
        return mDef.isRendable;
    }

    public boolean isRended() {
        return mIsRended;
    }

    public void setRended(boolean aRended) {
        mIsRended = aRended;
    }

    @Override
    public void render(Batch batch) {
        TransformComponent transform = this.getComponent(TransformComponent.class);
        VisualComponent visual = this.getComponent(VisualComponent.class);
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);

        if (velocity != null) {
            if (velocity.y == 0 && velocity.x == 0) {
                mCurrentState = getState(mDef.defaultState);
                mStateTime = 0;
            } else {
                if (velocity.y == 0) {
                    mCurrentState = getState(velocity.x < 0 ? InteractionState.STATE_MOVE_LEFT : InteractionState.STATE_MOVE_RIGHT);
                } else {
                    double angle = Math.atan2(velocity.y, velocity.x);
                    double PI4 = Math.PI / 4;
                    if (angle < 0) {
                        angle += Math.PI * 2;
                    }

                    if (angle > 7 * PI4 || angle <= PI4) {
                        mCurrentState = getState(InteractionState.STATE_MOVE_RIGHT);
                    } else if (angle > PI4 && angle <= 3 * PI4) {
                        mCurrentState = getState(InteractionState.STATE_MOVE_UP);
                    } else if (angle > 3 * PI4 && angle <= 5 * PI4) {
                        mCurrentState = getState(InteractionState.STATE_MOVE_LEFT);
                    } else if (angle > 5 * PI4 && angle <= 7 * PI4) {
                        mCurrentState = getState(InteractionState.STATE_MOVE_DOWN);
                    }
                }
            }
        }
        mCurrentFrame = mCurrentState.getTextureRegion(mStateTime);

        visual.region = mCurrentFrame;
        float width = visual.region.getRegionWidth();
        float height = visual.region.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;

        batch.draw(visual.region,
                transform.position.x + transform.originOffset.x, transform.position.y + transform.originOffset.y,
                originX, originY,
                width, height,
                transform.scale, transform.scale,
                transform.angle);
    }

    public Shape createShape() {
        return new PolygonShape();
    }
    @Override
    public Shape getShape() {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        mShape.setX(tfm.position.x + tfm.originOffset.x);
        mShape.setY(tfm.position.y + tfm.originOffset.y);

        if(isRendable()) {
            float width = mCurrentFrame.getRegionWidth() * tfm.scale;
            float height = mCurrentFrame.getRegionHeight() * tfm.scale;


            mVertices[0] = tfm.originOffset.x;
            mVertices[1] = tfm.originOffset.y;
            mVertices[2] = width + tfm.originOffset.x;
            mVertices[3] = tfm.originOffset.y;
            mVertices[4] = width + tfm.originOffset.x;
            mVertices[5] = height + tfm.originOffset.y;
            mVertices[6] = tfm.originOffset.x;
            mVertices[7] = height + tfm.originOffset.y;
            if (mShape.getType() == Shape.Type.POLYGON) {
                ((PolygonShape) mShape).getShape().setVertices(mVertices);
            }

            mShape.setX(tfm.position.x);
            mShape.setY(tfm.position.y);
        }
        return mShape;
    }


    @Override
    public void update(float dt) {
        CollisionComponent collision = this.getComponent(CollisionComponent.class);
        collision.mShape = getShape();
        mStateTime+=dt;
    }

    /*****************
     * COLLISION
     *******************************/
    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {

        if (!mCollisions.contains(aEntity, false)) {
            boolean ret = hasCollisionInteraction(aEntity);
            if (ret) {
                mCollisions.add(aEntity);
                if (mDef.isClickable) {
                    add(new InputComponent(this));
                }

                onStartCollisionInteraction(aEntity);
            }
            return ret;
        }
        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        if (mCollisions.contains(aEntity, false)) {
            mCollisions.removeValue(aEntity, false);
            if (mDef.isClickable) {
                remove(InputComponent.class);
            }
            onStopCollisionInteraction(aEntity);
            return true;
        }
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }

    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return false;
    }

    public void onStartCollisionInteraction(CollisionComponent aEntity) {

    }
    public void onStopCollisionInteraction(CollisionComponent aEntity) {

    }

    /*****************
     * TOUCH
     *******************************/

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

        mCamera.unproject(cursorPoint.set(screenX, screenY, 0));

        if (hasTouchInteraction(cursorPoint.x, cursorPoint.y)) {
            onTouchInteraction();
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

    protected boolean hasTouchInteraction(float x, float y) {
        return true;
    }

    public void onTouchInteraction() {

    }


    @Override
    public void onInteractionEvent(InteractionEvent aEvent) {
        if (mEventsAction != null && aEvent != null) {
            for (InteractionEventAction action : mEventsAction) {
                if (action.inputEvents != null) {
                    boolean performed = false;
                    for (InteractionEvent expectedEvent : action.inputEvents) {
                        if ((expectedEvent.sourceId==null || expectedEvent.sourceId.isEmpty()  || expectedEvent.sourceId.equals(aEvent.sourceId)) && expectedEvent.type.equals(aEvent.type) && expectedEvent.value.equals(aEvent.value)) {
                            expectedEvent.setPerformed(true);
                            performed = true;
                            break;
                        }
                    }
                    if (performed) {
                        boolean allPerformed = true;
                        for (InteractionEvent expectedEvent : action.inputEvents) {
                            if (!expectedEvent.isPerformed()) {
                                allPerformed = false;
                                break;
                            }
                        }
                        if (allPerformed) {
                            doActionOnEvent(action);
                        }
                    }
                }
            }
        }
    }

    protected void doActionOnEvent(InteractionEventAction aAction) {

    }
}
