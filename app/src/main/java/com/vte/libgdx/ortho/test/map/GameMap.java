package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.audio.AudioEvent;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.box2d.PathHero;
import com.vte.libgdx.ortho.test.box2d.PathMap;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.CollisionEffectComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionInteractionComponent;
import com.vte.libgdx.ortho.test.entity.components.ICollisionObstacleHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionObstacleComponent;
import com.vte.libgdx.ortho.test.entity.components.InteractionComponent;
import com.vte.libgdx.ortho.test.entity.components.PathComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.interactions.IInteraction;
import com.vte.libgdx.ortho.test.interactions.InteractionFactory;
import com.vte.libgdx.ortho.test.interactions.InteractionMapping;
import com.vte.libgdx.ortho.test.interactions.InteractionMappingManager;
import com.vte.libgdx.ortho.test.interactions.InteractionPortal;
import com.vte.libgdx.ortho.test.persistence.LocationProfile;
import com.vte.libgdx.ortho.test.persistence.MapProfile;
import com.vte.libgdx.ortho.test.persistence.PersistenceProvider;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.player.Player;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by vincent on 20/01/2017.
 */

public class GameMap implements ICollisionObstacleHandler {
    public final static String TAG = GameMap.class.getSimpleName();
    private TiledMap map;
    private String mMapName;
    private String mFromMapId;
    // private IsoTileMapRendererWithSprites renderer;
    private MapAndSpritesRenderer2 renderer;
    private OrthographicCamera mCamera;

    private HashMap<String, Array<Shape>> mBodiesZindex = new HashMap<String, Array<Shape>>();
    private Array<Shape> mBodiesCollision = new Array<Shape>();


    private Array<CollisionObstacleComponent> mCollisions = new Array<CollisionObstacleComponent>();
    private Array<IItemInteraction> mItems = new Array<IItemInteraction>();
    private Array<IInteraction> mInteractions = new Array<IInteraction>();
    private HashMap<String, PathMap> mPaths = new HashMap<String, PathMap>();
    private InteractionMappingManager mInteractionMappingManager = new InteractionMappingManager();

    private boolean mIsInitialized = false;

    private Player mPlayer;
    private String mMusic;


    public GameMap(String aMapName, String aFromMap, OrthographicCamera aCamera, MapTownPortalInfo aTownPortalInfo) {
        mMapName = aMapName;
        mFromMapId = aFromMap;
        boolean firstMapEntrance = false;
        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        if (mapProfile == null) {
            firstMapEntrance = true;
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

        if (PersistenceProvider.getInstance().getSettings().musicActivated) {
            MapProperties mapProperties = map.getProperties();
            mMusic = mapProperties.get("music", String.class);
            if (mMusic != null && !mMusic.isEmpty()) {
                AudioManager.getInstance().onAudioEvent(new AudioEvent(AudioEvent.Type.MUSIC_LOAD, mMusic));
            }
        }

        mPlayer = new Player(this);
        mPlayer.getHero().setCamera(mCamera);
        mPlayer.getHero().setPath(null);
        mPaths = buildPaths(map, "path");
        mItems = buildItems(map, "items");
        mInteractionMappingManager.loadMappingFile("data/interactions/" + aMapName + "_interactions_mapping.json");
        mInteractions = buildMapInteractions(map, "interactions");

        boolean isMapBackWithTownPortal = aTownPortalInfo != null && aTownPortalInfo.originMap.compareTo(getMapName()) == 0;
        boolean isCurrentMapIsDefault = getMapName().compareTo(MyGame.DEFAULT_MAP_NAME) == 0;


        LocationProfile locationProfile = new LocationProfile();
        locationProfile.mMapId = mMapName;


        if (isMapBackWithTownPortal) {
            // set hero at the town portal position
            mPlayer.getHero().setPosition(aTownPortalInfo.x, aTownPortalInfo.y);
            tryToSetCameraAtPosition(aTownPortalInfo.x, aTownPortalInfo.y);
            mPlayer.getHero().launchTownPortalArrivalEffect(aTownPortalInfo);
        }
        if (aTownPortalInfo == null) {
            locationProfile.mFromMapId = aFromMap;
        }
        Profile.getInstance().setLocationProfile(locationProfile);


        for (IInteraction control : mInteractions) {
            if (control.getType() == IInteraction.Type.PORTAL) {
                InteractionPortal portal = (InteractionPortal) control;

                boolean isDefaultCase = aFromMap == null && portal.isDefaultStart();
                boolean isPortalToPortalCase = aTownPortalInfo == null && aFromMap != null && portal.getTargetMapId() != null && portal.getTargetMapId().compareTo(aFromMap) == 0;
                boolean isDefaultMapArrivalFromTownPortal = aTownPortalInfo != null && isCurrentMapIsDefault && portal.isDefaultStart();
                if (isDefaultCase || isPortalToPortalCase || isDefaultMapArrivalFromTownPortal) {
                    mPlayer.getHero().setPosition(control.getX(), control.getY());
                    tryToSetCameraAtPosition(control.getX(), control.getY());
                    if (isDefaultMapArrivalFromTownPortal) {
                        mPlayer.getHero().launchTownPortalArrivalEffect(aTownPortalInfo);
                    }
                    portal.setActivated(false);

                    if (portal.getQuestId() != null) {
                        Quest theQuest = QuestManager.getInstance().getQuestFromId(portal.getQuestId());
                        if (theQuest != null && !theQuest.isCompleted()) {
                            theQuest.setActivated(true);
                            EventDispatcher.getInstance().onQuestActivated(theQuest);
                        }
                    }
                    //  Gdx.app.debug("DEBUG", "init camera pointX=" + mCamera.position.x + " pointY=" + mCamera.position.y);


                } else {
                    if (aTownPortalInfo != null) {
                        // check hero is not on the portal
                        if (ShapeUtils.overlaps(mPlayer.getHero().getShapeInteraction(), portal.getShapeInteraction())) {
                            portal.setActivated(false);
                        } else {
                            portal.setActivated(true);
                        }
                    } else {
                        portal.setActivated(true);
                    }
                }
            }
        }

        renderer = new MapAndSpritesRenderer2(this, MyGame.SCALE_FACTOR);
       // mBodiesZindex = buildShapes(map, "zindex");
        for(MapLayer layer : map.getLayers())
        {
            if(layer.getName().startsWith("zindex_"))
            {
                mBodiesZindex.put(layer.getName().substring("zindex_".length(), layer.getName().length()), buildShapes(map, layer.getName()));
            }
            else if(layer.getName().compareTo("zindex")==0)
            {
                mBodiesZindex.put("zindex", buildShapes(map, layer.getName()));
            }
        }
        if(!mBodiesZindex.containsKey("zindex"))
        {
            mBodiesZindex.put("zindex", new Array());
        }
        mBodiesCollision = buildShapes(map, "collision");

        if (mPaths.containsKey("hero") && firstMapEntrance) {
            mPlayer.getHero().setPath(new PathHero(mPaths.get("hero")));
        }
        mIsInitialized = true;
    }


    protected void tryToSetCameraAtPosition(float x, float y) {
        mCamera.position.x = x;
        mCamera.position.y = y;
        MapProperties mapProperties = map.getProperties();
        int mapWidth = mapProperties.get("width", Integer.class);
        int mapTileWidth = mapProperties.get("tilewidth", Integer.class);
        int mapHeight = mapProperties.get("height", Integer.class);
        int mapTileHeight = mapProperties.get("tileheight", Integer.class);
        mapWidth = (int) (mapWidth * mapTileWidth * MyGame.SCALE_FACTOR);
        mapHeight = (int) (mapHeight * mapTileHeight * MyGame.SCALE_FACTOR);
        if (x + (mCamera.viewportWidth / 2) > mapWidth) {
            mCamera.position.x = mapWidth - (mCamera.viewportWidth / 2);
        } else if (x - (mCamera.viewportWidth / 2) < 0) {
            mCamera.position.x = mCamera.viewportWidth / 2;
        }
        if (y + (mCamera.viewportHeight / 2) > mapHeight) {
            mCamera.position.y = mapHeight - (mCamera.viewportHeight / 2);
        } else if (y - (mCamera.viewportHeight / 2) < 0) {
            mCamera.position.y = mCamera.viewportHeight / 2;
        }
        mCamera.update();
    }

    public String getMapName() {
        return mMapName;
    }

    public String getFromMapId()
    {
        return mFromMapId;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public void playMusic(boolean aPlay) {
        if (mMusic != null && !mMusic.isEmpty()) {
            AudioManager.getInstance().onAudioEvent(new AudioEvent(aPlay ? AudioEvent.Type.MUSIC_PLAY_LOOP : AudioEvent.Type.MUSIC_STOP, mMusic));
        }
    }

    public void destroy() {
        ImmutableArray<Entity> entities = EntityEngine.getInstance().getEntitiesFor(Family.one(CollisionObstacleComponent.class, CollisionInteractionComponent.class, CollisionEffectComponent.class, InteractionComponent.class, PathComponent.class, TransformComponent.class, VelocityComponent.class, VisualComponent.class).get());
        int size = entities.size();
        for (int i = size - 1; i >= 0; i--) {
            EntityEngine.getInstance().removeEntity(entities.get(i));
        }
        for (IInteraction it : mInteractions) {
            it.destroy();
        }
        for (IItemInteraction it : mItems) {
            it.destroy();
        }
        mBodiesCollision.clear();
        mItems.clear();
        mBodiesZindex.clear();
        mInteractions.clear();
        String filename = "data/maps/" + mMapName + ".tmx";
        AssetsUtility.unloadAsset(filename);
    }

    public void render() {
        if (!mIsInitialized)
            return;
        MapProperties mapProperties = map.getProperties();
        int mapWidth = mapProperties.get("width", Integer.class);
        int mapTileWidth = mapProperties.get("tilewidth", Integer.class);
        int mapHeight = mapProperties.get("height", Integer.class);
        int mapTileHeight = mapProperties.get("tileheight", Integer.class);
        mapWidth = (int) (mapWidth * mapTileWidth * MyGame.SCALE_FACTOR);
        mapHeight = (int) (mapHeight * mapTileHeight * MyGame.SCALE_FACTOR);
        if (mPlayer.getHero().getPosition().x > mCamera.viewportWidth / 2 && mPlayer.getHero().getPosition().x < mapWidth - (mCamera.viewportWidth / 2)) {
            mCamera.position.x = mPlayer.getHero().getPosition().x;
        }
        if (mPlayer.getHero().getPosition().y > mCamera.viewportHeight / 2 && mPlayer.getHero().getPosition().y < mapHeight - (mCamera.viewportHeight / 2)) {
            mCamera.position.y = mPlayer.getHero().getPosition().y;
        }

        mCamera.update();
        renderer.setView(mCamera);
        renderer.render();
    }


    public TiledMap getTiledMap() {
        return map;
    }

    public Array<Shape> getBodiesZindex(String aLayerName) {
        if(mBodiesZindex.containsKey(aLayerName))
            return mBodiesZindex.get(aLayerName);
        else
            return mBodiesZindex.get("zindex");
    }

    public Array<Shape> getBodiesCollision() {
        return mBodiesCollision;
    }

    public Array<IItemInteraction> getItems() {
        return mItems;
    }

    public Array<IInteraction> getInteractions() {
        return mInteractions;
    }


    public HashMap<String, PathMap> getPaths() {
        return mPaths;
    }

    @Override
    public boolean onCollisionObstacleStart(CollisionObstacleComponent aEntity) {
        return false;
    }

    @Override
    public boolean onCollisionObstacleStop(CollisionObstacleComponent aEntity) {
        return false;
    }

    @Override
    public Array<CollisionObstacleComponent> getCollisionObstacle() {
        return mCollisions;
    }

    private Array<Shape> buildShapes(Map map, String layerName) {
        boolean isCollision = layerName.contains("zindex") ? false : true;

        Array<Shape> bodies = new Array<Shape>();
        if (map.getLayers().get(layerName) == null)
            return bodies;

        MapObjects objects = map.getLayers().get(layerName).getObjects();

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
                float[] vertices = new float[mapVertices.length]; // last point is the first
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
            PolygonShape shape = new PolygonShape();
            shape.setShape(polygon);
            bodies.add(shape);
            if (isCollision && polygon != null) {

                Entity entity = new Entity();
                entity.add(new CollisionObstacleComponent(CollisionObstacleComponent.OBSTACLE, shape, object.getName(), this, this));
                EntityEngine.getInstance().addEntity(entity);
            }

        }
        Shape[] sortedShapes = bodies.toArray(Shape.class);
        Arrays.sort(sortedShapes, new Comparator<Shape>() {
            @Override
            public int compare(Shape lhs, Shape rhs) {

                float lhsY = lhs.getBounds().getY();
                float rhsY = rhs.getBounds().getY();
                if (lhsY == rhsY) {
                    return 0;
                } else {
                    return lhsY < rhsY ? 1 : -1;
                }
            }
        });
        bodies.clear();
        bodies = new Array<Shape>(sortedShapes);
        return bodies;
    }

    private HashMap<String, PathMap> buildPaths(Map map, String layerName) {
        HashMap<String, PathMap> paths = new HashMap<String, PathMap>();

        if (map.getLayers().get(layerName) == null)
            return paths;
        MapObjects objects = map.getLayers().get(layerName).getObjects();


        for (MapObject object : objects) {


            if (object instanceof PolylineMapObject) {
                float[] mapVertices = ((PolylineMapObject) object).getPolyline().getTransformedVertices();
                float[] vertices = new float[mapVertices.length];
                PathMap path = new PathMap();
                for (int i = 0; i < vertices.length - 1; i += 2) {
                    path.addPoint(mapVertices[i] * MyGame.SCALE_FACTOR, mapVertices[i + 1] * MyGame.SCALE_FACTOR);
                }
                boolean isLoop = false;
                if (object.getProperties().containsKey("isLoop")) {
                    isLoop = Boolean.parseBoolean(object.getProperties().get("isLoop", String.class));
                }
                path.setLoop(isLoop);
                paths.put(object.getName(), path);

            } else {
                continue;
            }
        }
        return paths;
    }

    private Array<IInteraction> buildMapInteractions(Map map, String layerName) {
        Array<IInteraction> interactions = new Array<IInteraction>();
        if(map.getLayers().get(layerName)==null)
        {
            return interactions;
        }
        MapObjects objects = map.getLayers().get(layerName).getObjects();

        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;
                float x = textureObject.getX() * MyGame.SCALE_FACTOR;
                float y = textureObject.getY() * MyGame.SCALE_FACTOR;
                InteractionMapping mapping = mInteractionMappingManager.getInterationMapping(object.getName());
                if (mapping == null) {
                    continue;
                }
                IInteraction interaction = InteractionFactory.getInstance().createInteractionInstance(x, y, mapping, textureObject.getProperties(), this);
                if (interaction != null) {
                    interaction.setCamera(mCamera);
                    interactions.add(interaction);
                }

            }
        }
        return interactions;
    }

    private Array<IItemInteraction> buildItems(Map map, String layerName) {
        Array<IItemInteraction> interactions = new Array<IItemInteraction>();
        if(map.getLayers().get(layerName)==null)
        {
            return interactions;
        }
        MapObjects objects = map.getLayers().get(layerName).getObjects();



        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;
                float x = textureObject.getX() * MyGame.SCALE_FACTOR;
                float y = textureObject.getY()  * MyGame.SCALE_FACTOR;
                String type = textureObject.getProperties().get("type", String.class);
                if (type == null) {
                    continue;
                }
                IItemInteraction interaction = null;
                if (type.compareTo(IItemInteraction.Type.ITEM.name()) == 0) {
                    boolean itemAlreadyFound = false;
                    if (mapProfile != null) {
                        ArrayList<Vector2> itemsFound = mapProfile.items.get(textureObject.getName());
                        if (itemsFound != null) {
                            for (Vector2 v : itemsFound) {
                                if (Math.abs(v.x - x) <= 0.2 && Math.abs(v.y - y) <= 0.2) {
                                    itemAlreadyFound = true;
                                    break;

                                }
                            }
                        }
                    }
                    if (!itemAlreadyFound) {
                        interaction = new ItemInteraction(x, y, textureObject.getName(), this);
                    }

                }


            }

        }
        Profile.getInstance().getMapProfile(mMapName);
        return interactions;
    }

    public void removeItem(ItemInteraction aItem) {
        mItems.removeValue(aItem, true);
        MapProfile mapProfile = Profile.getInstance().getMapProfile(mMapName);
        ArrayList<Vector2> itemsFound = mapProfile.items.get(aItem.getId());
        if (itemsFound == null) {
            itemsFound = new ArrayList<Vector2>();
        }
        boolean itemAlreadyFound = false;
        for (Vector2 v : itemsFound) {
            if (Math.abs(v.x - aItem.getX()) <= 0.2 && Math.abs(v.y - aItem.getY()) <= 0.2) {
                itemAlreadyFound = true;
                break;
            }
        }
        if (!itemAlreadyFound) {
            itemsFound.add(new Vector2(aItem.getX(), aItem.getY()));
        }
        mapProfile.items.put(aItem.getId(), itemsFound);
        Profile.getInstance().updateMapProfile(mMapName, mapProfile);
    }
}
