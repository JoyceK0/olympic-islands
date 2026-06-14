package com.github.JoyceK0.Olympic_Islands.tiled;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.JoyceK0.Olympic_Islands.GdxGame;
import com.github.JoyceK0.Olympic_Islands.asset.AssetService;
import com.github.JoyceK0.Olympic_Islands.asset.AtlasAsset;
import com.github.JoyceK0.Olympic_Islands.component.Facing.FacingDirection;
import com.github.JoyceK0.Olympic_Islands.component.Animation2D.AnimationType;
import com.github.JoyceK0.Olympic_Islands.component.*;
import com.github.JoyceK0.Olympic_Islands.component.Transform;

public class TiledAshleyConfigurator { // Contains custom logic for the game rather than using listeners, coordinates the various services used to run game logic

    private static final Vector2 DEFAULT_PHYSICS_SCALING = new Vector2(1f, 1f);

    private final Engine engine;
    private final AssetService assetService;
    private final World physicWorld;

    //Constructor
    public TiledAshleyConfigurator(Engine engine, AssetService assetService, World physicWorld) {
        this.engine = engine;
        this.assetService = assetService;
        this.physicWorld = physicWorld;
    }

    public void onLoadTile(TiledMapTile tiledMapTile, float x, float y) {
        createBody(
            tiledMapTile.getObjects(),
            new Vector2(x, y),
            DEFAULT_PHYSICS_SCALING,
            BodyDef.BodyType.StaticBody,
            Vector2.Zero, // relative position, how the tile's position is related to the cell's coordinates around it
            "environment" // arbitrary data to add to body and its various fixtures
        );
    }

    private Body createBody(MapObjects mapObjects,
                            Vector2 position,
                            Vector2 scaling,
                            BodyDef.BodyType bodyType,
                            Vector2 relativeTo,
                            Object userData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType; // three different types, static, kinematic, and dynamic. Static is like background objects such as trees and houses, kinematic useful for platformers and moving objects, and dynamic for player movement
        bodyDef.position.set(position);
        bodyDef.fixedRotation = true; // body cannot rotate, improves processing speed

        Body body = physicWorld.createBody(bodyDef);
        body.setUserData(userData);

        for(MapObject object : mapObjects) {
            FixtureDef fixtureDef = TiledPhysics.fixtureDefOf(object, scaling, relativeTo);
            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(object.getName()); // if the collision object/fixture was assigned any name, it would be here
            fixtureDef.shape.dispose(); // once the fixture and name is created, the temporary shape variable can be disposed of
        }

        return body;
    }

    public void onLoadObject(TiledMapTileMapObject tileMapObject) { // grabs object, its tile, and image references
        Entity entity = this.engine.createEntity();
        TiledMapTile tile = tileMapObject.getTile();
        TextureRegion textureRegion = getTextureRegion(tile); // the image reference and properties linked to the tile
        int z = tile.getProperties().get("z", 1, Integer.class);

        entity.add(new Graphic(Color.WHITE.cpy(), textureRegion));
        addEntityTransform(
            tileMapObject.getX(), tileMapObject.getY(), z,
            textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
            tileMapObject.getScaleX(), tileMapObject.getScaleY(),
            entity
        );

        addEntityController(tileMapObject, entity); // process and display keyboard movement
        addEntityMove(tile, entity);
        addEntityAnimation(tile, entity);
        BodyDef.BodyType bodyType = getObjectBodyType(tile);
        addEntityPhysic(tile.getObjects(), bodyType, Vector2.Zero, entity);
        entity.add(new Facing(FacingDirection.DOWN));
        entity.add(new Fsm(entity));


        addEntityCameraFollow(tileMapObject, entity);

        this.engine.addEntity(entity);
    }

    private BodyDef.BodyType getObjectBodyType(TiledMapTile tile) {
        String classType = tile.getProperties().get("type", "", String.class);
        if("Prop".equals(classType)) {
            return BodyDef.BodyType.StaticBody;
        }
        return BodyDef.BodyType.DynamicBody;
    }

    private void addEntityPhysic(MapObjects objects, BodyDef.BodyType bodyType, Vector2 relativeTo, Entity entity) {
        if(objects.getCount() == 0) return; // no collision tiles/ objects

        Transform transform = Transform.MAPPER.get(entity);
        Body body = createBody(objects, transform.getPosition(), transform.getScaling(), bodyType, relativeTo, entity);
        entity.add(new Physic(body, transform.getPosition().cpy()));
    }

    private void addEntityAnimation(TiledMapTile tile, Entity entity) {
        String animationStr = tile.getProperties().get("animation", "", String.class);
        if (animationStr.isBlank()) return; // return if object has no animation

        AnimationType animationType = AnimationType.valueOf(animationStr);
        String atlasAssetStr = tile.getProperties().get("atlasAsset", "OBJECTS", String.class);
        AtlasAsset atlasAsset = AtlasAsset.valueOf(atlasAssetStr);
        FileTextureData textureData = (FileTextureData) tile.getTextureRegion().getTexture().getTextureData();
        String atlasKey = textureData.getFileHandle().nameWithoutExtension();
        float speed = tile.getProperties().get("animationSpeed", 0f, Float.class);
        entity.add(new Animation2D(atlasAsset, atlasKey, animationType, PlayMode.LOOP, speed));
    }

    private void addEntityCameraFollow(TiledMapTileMapObject mapObject, Entity entity){
        boolean camFollow = mapObject.getProperties().get("camFollow", false,Boolean.class);
        if(!camFollow) return;
        entity.add(new CameraFollow());

    }

    private void addEntityMove(TiledMapTile tile, Entity entity) {
        Float speed = tile.getProperties().get("speed", 0f, Float.class);
        if(speed == 0f) return;

        entity.add(new Move(speed));
    }

    private void addEntityController(TiledMapTileMapObject tileMapObject, Entity entity) {
        boolean controller = tileMapObject.getProperties().get("controller", false, Boolean.class);
        if(!controller) return;

        entity.add(new Controller());
    }

    private void addEntityTransform(
        float x, float y, int z,
        float w, float h,
        float scaleX, float scaleY,
        Entity entity
    ) {
        Vector2 position = new Vector2(x, y);
        Vector2 size = new Vector2(w, h);
        Vector2 scaling = new Vector2(scaleX, scaleY);

        //Convert from pixels coordinates to abstract game screen unit scale for window scalability
        position.scl(GdxGame.UNIT_SCALE);
        size.scl(GdxGame.UNIT_SCALE);

        entity.add(new Transform(position, z, size, scaling, 0f));
    }

    private TextureRegion getTextureRegion(TiledMapTile tile) {
        String atlasAssetStr = tile.getProperties().get("atlasAsset", AtlasAsset.OBJECTS.name(), String.class);
        AtlasAsset atlasAsset = AtlasAsset.valueOf(atlasAssetStr);
        TextureAtlas textureAtlas = this.assetService.get(atlasAsset);
        FileTextureData textureData = (FileTextureData) tile.getTextureRegion().getTexture().getTextureData();
        String atlasKey = textureData.getFileHandle().nameWithoutExtension();
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(atlasKey + "/" + atlasKey);
        if(region != null) {
            return region;
        }
        // if it is null then we default to the tiled tilemap and not the atlas tilemap just so we get something showing on screen
        return tile.getTextureRegion();
    }

}
