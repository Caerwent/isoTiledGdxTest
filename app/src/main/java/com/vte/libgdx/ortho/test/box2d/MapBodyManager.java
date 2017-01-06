package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.characters.CharactersManager;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.map.DefaultMapInteraction;
import com.vte.libgdx.ortho.test.map.IMapInteraction;
import com.vte.libgdx.ortho.test.map.MapInteractionItem;
import com.vte.libgdx.ortho.test.map.MapInteractionNPJ;

/**
 * Created by vincent on 07/07/2016.
 */

public class MapBodyManager implements ICollisionHandler {


    static private MapBodyManager s_instance;

    public static MapBodyManager getInstance() {
        return s_instance;
    }

    public static synchronized MapBodyManager createInstance(Map map) {
        s_instance = new MapBodyManager(map);
        return s_instance;
    }

    private Array<Shape> mBodiesZindex = new Array<Shape>();
    private Array<Shape> mBodiesCollision = new Array<Shape>();


    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();
    private Array<IMapInteraction> mInteractions = new Array<IMapInteraction>();


    public MapBodyManager(Map map) {
        mBodiesZindex = buildShapes(map, "zindex");
        mBodiesCollision = buildShapes(map, "collision");
        mInteractions = buildInteractions(map, "interaction");

    }

    public Array<Shape> getBodiesZindex() {
        return mBodiesZindex;
    }

    public Array<Shape> getBodiesCollision() {
        return mBodiesCollision;
    }

    public Array<Shape> buildShapes(Map map, String layerName) {
        CollisionComponent.Type type = "zindex".compareTo(layerName) == 0 ? CollisionComponent.Type.ZINDEX : CollisionComponent.Type.OBSTACLE;
        MapObjects objects = map.getLayers().get(layerName).getObjects();

        Array<Shape> bodies = new Array<Shape>();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            Polygon polygon = null;

            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                polygon = new Polygon(new float[]{0, 0, rect.getWidth() * MyGame.SCALE_FACTOR, 0, rect.getWidth() * MyGame.SCALE_FACTOR, rect.getHeight() * MyGame.SCALE_FACTOR, 0, rect.getHeight() * MyGame.SCALE_FACTOR});
                polygon.setPosition(rect.x * MyGame.SCALE_FACTOR, rect.y * MyGame.SCALE_FACTOR);
            } else if (object instanceof PolygonMapObject) {
                float[] mapVertices = ((PolygonMapObject) object).getPolygon().getTransformedVertices();
                float[] vertices = new float[mapVertices.length - 2];
                for (int i = 0; i < vertices.length; ++i) {
                    vertices[i] = mapVertices[i] * MyGame.SCALE_FACTOR;
                }
                polygon = new Polygon(vertices);
            }/* else if (object instanceof PolylineMapObject) {
                shape = ShapeFactory.getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = ShapeFactory.getCircle((CircleMapObject) object);
            }*/ else {
                continue;
            }

            if (polygon != null) {

                Entity entity = new Entity();
                PolygonShape shape = new PolygonShape();
                shape.setShape(polygon);
                bodies.add(shape);
                entity.add(new CollisionComponent(type, shape, object.getName(), this, this));
                EntityEngine.getInstance().addEntity(entity);
            }

        }
        return bodies;
    }

    public Array<IMapInteraction> getInteractions() {
        return mInteractions;
    }


    public Array<IMapInteraction> buildInteractions(Map map, String layerName) {
        MapObjects objects = map.getLayers().get(layerName).getObjects();

        Array<IMapInteraction> interactions = new Array<IMapInteraction>();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;
                float x = (textureObject.getX() + textureObject.getTextureRegion().getRegionWidth() / 2) * MyGame.SCALE_FACTOR;
                float y = (textureObject.getY() + textureObject.getTextureRegion().getRegionHeight() / 2) * MyGame.SCALE_FACTOR;
                String type = textureObject.getProperties().get("type", String.class);
                if (type == null) {
                    continue;
                }
                if (type.compareTo(IMapInteraction.Type.START.name()) == 0) {
                    IMapInteraction control = new DefaultMapInteraction(x, y, IMapInteraction.Type.START);
                    interactions.add(control);
                } else if (type.compareTo(IMapInteraction.Type.ITEM.name()) == 0) {
                    MapInteractionItem item = new MapInteractionItem(x, y, textureObject.getName());
                    interactions.add(item);
                }
                else if (type.compareTo(IMapInteraction.Type.NPJ.name()) == 0) {
                    MapInteractionNPJ pnj = new MapInteractionNPJ(x, y, CharactersManager.getInstance().getCharactersFactory().getCharacterDefById(textureObject.getName()));
                    interactions.add(pnj);
                }

            }

        }
        return interactions;
    }

    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
