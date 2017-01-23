package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.characters.CharactersManager;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.persistence.MapProfile;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.player.Player;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestManager;

import java.util.ArrayList;

/**
 * Created by vincent on 20/01/2017.
 */

public class GameMap implements ICollisionHandler {
    public final static String TAG = GameMap.class.getSimpleName();
    private TiledMap map;
    private String mMapName;
    // private IsoTileMapRendererWithSprites renderer;
    private MapAndSpritesRenderer2 renderer;
    private OrthographicCamera mCamera;

    private Array<Shape> mBodiesZindex = new Array<Shape>();
    private Array<Shape> mBodiesCollision = new Array<Shape>();


    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();
    private Array<IMapInteraction> mInteractions = new Array<IMapInteraction>();

    public GameMap(String aMapName, OrthographicCamera aCamera) {
        mMapName = aMapName;
        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        if (mapProfile == null) {
            mapProfile = new MapProfile();
            Profile.getInstance().updateMapProfile(mMapName, mapProfile);
        }
        String filename = "data/maps/" + aMapName + ".tmx";
        AssetsUtility.loadMapAsset(filename);
        if (AssetsUtility.isAssetLoaded(filename)) {
            map = AssetsUtility.getMapAsset(filename);
        } else {
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        }
        mCamera = aCamera;
        renderer = new MapAndSpritesRenderer2(this, MyGame.SCALE_FACTOR);
        mBodiesZindex = buildShapes(map, "zindex");
        mBodiesCollision = buildShapes(map, "collision");
        mInteractions = buildInteractions(map, "interaction");
        renderer.addSprite(Player.getInstance().getHero());
        for (IMapInteraction control : mInteractions) {
            if (control.getInteractionType() == IMapInteraction.Type.START) {
                Player.getInstance().getHero().setPosition(control.getX(), control.getY());
                if (control.getQuestId() != null) {
                    Quest theQuest = QuestManager.getInstance().getQuestFromId(control.getQuestId());
                    if (theQuest != null && !theQuest.isCompleted()) {
                        theQuest.setActivated(true);
                        EventDispatcher.getInstance().onQuestActivated(theQuest);
                    }
                }
                break;
            }
        }
    }

    public void render() {
        MapProperties mapProperties = map.getProperties();
        int mapWidth = mapProperties.get("width", Integer.class);
        int mapTileWidth = mapProperties.get("tileheight", Integer.class);
        int mapHeight = mapProperties.get("width", Integer.class);
        int mapTileHeight = mapProperties.get("tileheight", Integer.class);
        mapWidth = (int) (mapWidth * mapTileWidth * MyGame.SCALE_FACTOR);
        mapHeight = (int) (mapHeight * mapTileHeight * MyGame.SCALE_FACTOR);
        if (Player.getInstance().getHero().getPosition().x > mCamera.viewportWidth / 2 && Player.getInstance().getHero().getPosition().x < mapWidth - (mCamera.viewportWidth / 2)) {
            mCamera.position.x = Player.getInstance().getHero().getPosition().x;
        }
        if (Player.getInstance().getHero().getPosition().y > mCamera.viewportHeight / 2 && Player.getInstance().getHero().getPosition().y < mapHeight - (mCamera.viewportHeight / 2)) {
            mCamera.position.y = Player.getInstance().getHero().getPosition().y;
        }

        mCamera.update();
        renderer.setView(mCamera);
        renderer.render();
    }


    public TiledMap getTiledMap() {
        return map;
    }

    public Array<Shape> getBodiesZindex() {
        return mBodiesZindex;
    }

    public Array<Shape> getBodiesCollision() {
        return mBodiesCollision;
    }

    public Array<IMapInteraction> getInteractions() {
        return mInteractions;
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

    private Array<Shape> buildShapes(Map map, String layerName) {
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

    private Array<IMapInteraction> buildInteractions(Map map, String layerName) {
        MapObjects objects = map.getLayers().get(layerName).getObjects();

        Array<IMapInteraction> interactions = new Array<IMapInteraction>();

        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;
                float x = (textureObject.getX() + textureObject.getTextureRegion().getRegionWidth() / 2) * MyGame.SCALE_FACTOR;
                float y = (textureObject.getY() + textureObject.getTextureRegion().getRegionHeight() / 2) * MyGame.SCALE_FACTOR;
                String type = textureObject.getProperties().get("type", String.class);
                if (type == null) {
                    continue;
                }
                IMapInteraction interaction = null;
                if (type.compareTo(IMapInteraction.Type.START.name()) == 0) {
                    interaction = new DefaultMapInteraction(x, y, IMapInteraction.Type.START);
                } else if (type.compareTo(IMapInteraction.Type.ITEM.name()) == 0) {
                    boolean itemAlreadyFound = false;
                    if(mapProfile!=null)
                    {
                        ArrayList<Vector2> itemsFound =  mapProfile.items.get(textureObject.getName());
                        if(itemsFound!=null)
                        {
                            for(Vector2 v : itemsFound)
                            {
                                if(Math.abs(v.x-x) <=0.2 && Math.abs(v.y-y)<=0.2)
                                {
                                    itemAlreadyFound = true;
                                    break;

                                }
                            }
                        }
                    }
                    if(!itemAlreadyFound)
                    {
                        interaction = new MapInteractionItem(x, y, textureObject.getName());
                    }

                } else if (type.compareTo(IMapInteraction.Type.NPJ.name()) == 0) {
                    interaction = new MapInteractionNPJ(x, y, CharactersManager.getInstance().getCharactersFactory().getCharacterDefById(textureObject.getName()));

                }
                if (interaction != null) {
                    String questId = textureObject.getProperties().get("questId", String.class);
                    interaction.setQuestId(questId);
                    interactions.add(interaction);
                }


            }

        }
        Profile.getInstance().getMapProfile(mMapName);
        return interactions;
    }

    public void removeItem(MapInteractionItem aItem) {
        mInteractions.removeValue(aItem, true);
        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        ArrayList<Vector2> itemsFound =  mapProfile.items.get(aItem.getId());
        if(itemsFound==null)
        {
            itemsFound = new ArrayList<Vector2>();
        }
        boolean itemAlreadyFound = false;
            for(Vector2 v : itemsFound)
            {
                if(Math.abs(v.x-aItem.getX()) <=0.2 && Math.abs(v.y-aItem.getY())<=0.2)
                {
                    itemAlreadyFound = true;
                    break;
                }
            }
        if(!itemAlreadyFound)
        {
            itemsFound.add(new Vector2(aItem.getX(), aItem.getY()));
        }
        mapProfile.items.put(aItem.getId(), itemsFound);
        Profile.getInstance().updateMapProfile(mMapName, mapProfile);
    }
}
