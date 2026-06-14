package com.github.JoyceK0.Olympic_Islands.tiled;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.JoyceK0.Olympic_Islands.GdxGame;

public final class TiledPhysics {

    // relativeTo is necessary for map objects that are directly placed on a layer (like rectangles for trigger).
    // Their x/y is equal to the position of the object, but we need it relative to 0,0 like it
    // is in the collision editor of a tile.

    public static FixtureDef fixtureDefOf(MapObject mapObject, Vector2 scaling, Vector2 relativeTo) {

        // return based on type of collision objects. in our game, it is mostly made up of custom polygons
        if (mapObject instanceof RectangleMapObject rectMapObj) {
            return rectangleFixtureDef(rectMapObj, scaling, relativeTo);
        } else if (mapObject instanceof EllipseMapObject ellipseMapObj) {
            return ellipseFixtureDef(ellipseMapObj, scaling, relativeTo);
        } else if (mapObject instanceof PolygonMapObject polygonMapObj) {
            Polygon polygon = polygonMapObj.getPolygon();
            float offsetX = polygon.getX() * GdxGame.UNIT_SCALE; // scale units according to game scale for custom polygon
            float offsetY = polygon.getY() * GdxGame.UNIT_SCALE;
            return polygonFixtureDef(polygonMapObj, polygon.getVertices(), offsetX, offsetY, scaling, relativeTo);
        } else if (mapObject instanceof PolylineMapObject polylineMapObj) {
            Polyline polyline = polylineMapObj.getPolyline();
            float offsetX = polyline.getX() * GdxGame.UNIT_SCALE;
            float offsetY = polyline.getY() * GdxGame.UNIT_SCALE;
            return polygonFixtureDef(polylineMapObj, polyline.getVertices(), offsetX, offsetY, scaling, relativeTo);
        } else {
            throw new GdxRuntimeException("Unsupported MapObject: " + mapObject);
        }

    }

    // Box is centered around body position in Box2D, but we want to have it aligned in a way
    // that the body position is the bottom left corner of the box.
    // That's why we use a 'boxOffset' below. This makes it easier for collision detection and rendering
    private static FixtureDef rectangleFixtureDef(RectangleMapObject mapObject, Vector2 scaling, Vector2 relativeTo) {
        Rectangle rectangle = mapObject.getRectangle();

        // get properties
        float rectX = rectangle.x;
        float rectY = rectangle.y;
        float rectW = rectangle.width;
        float rectH = rectangle.height;

        // need to center the fixture of the collision box on the character and apply appropriate scaling
        float boxX = rectX * GdxGame.UNIT_SCALE * scaling.x - relativeTo.x;
        float boxY = rectY * GdxGame.UNIT_SCALE * scaling.y - relativeTo.y;
        float boxW = rectW * GdxGame.UNIT_SCALE * scaling.x * 0.5f;
        float boxH = rectH * GdxGame.UNIT_SCALE * scaling.y * 0.5f;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxW, boxH, new Vector2(boxX + boxW, boxY + boxH), 0f);
        return fixtureDefOfMapObjectAndShape(mapObject, shape);
    }

    private static FixtureDef ellipseFixtureDef(EllipseMapObject mapObject, Vector2 scaling, Vector2 relativeTo) {
        Ellipse ellipse = mapObject.getEllipse();

        // get properties
        float x = ellipse.x;
        float y = ellipse.y;
        float w = ellipse.width;
        float h = ellipse.height;

        float ellipseX = x * GdxGame.UNIT_SCALE * scaling.x - relativeTo.x;
        float ellipseY = y * GdxGame.UNIT_SCALE * scaling.y - relativeTo.y;
        float ellipseW = w * GdxGame.UNIT_SCALE * scaling.x * 0.5f;
        float ellipseH = h * GdxGame.UNIT_SCALE * scaling.y * 0.5f;

        if (MathUtils.isEqual(ellipseW, ellipseH, 0.1f)) {
            // width and height are equal -> return a circle shape, simplifies and makes processing faster
            CircleShape shape = new CircleShape();
            shape.setPosition(new Vector2(ellipseX + ellipseW, ellipseY + ellipseH));
            shape.setRadius(ellipseW);
            return fixtureDefOfMapObjectAndShape(mapObject, shape);
        }

        // width and height are not equal -> return an ellipse shape (=polygon with 'numVertices' vertices)
        // PolygonShape only supports 8 vertices
        // ChainShape supports more but does not properly collide in some scenarios, therefore polygon is preffered
        final int numVertices = 8;
        float angleStep = MathUtils.PI2 / numVertices;
        Vector2[] vertices = new Vector2[numVertices];

        // since polygons can only have a max of up to 8 vertices for this method, calculations are run to determine those maximum of eight vertices
        for (int vertexIdx = 0; vertexIdx < numVertices; vertexIdx++) {
            float angle = vertexIdx * angleStep;
            float offsetX = ellipseW * MathUtils.cos(angle);
            float offsetY = ellipseH * MathUtils.sin(angle);
            vertices[vertexIdx] = new Vector2(ellipseX + ellipseW + offsetX, ellipseY + ellipseH + offsetY);
        }

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        return fixtureDefOfMapObjectAndShape(mapObject, shape);
    }

    private static FixtureDef polygonFixtureDef(
        MapObject mapObject, // Could be PolygonMapObject or PolylineMapObject
        float[] polyVertices,
        float offsetX,
        float offsetY,
        Vector2 scaling,
        Vector2 relativeTo
    ) {

        // set properties and scale accordingly using relative position and game scaling
        offsetX = (offsetX * scaling.x) - relativeTo.x;
        offsetY = (offsetY * scaling.y) - relativeTo.y;
        float[] vertices = new float[polyVertices.length];

        // for custom polygons, everything is defined by vertices, which are then connected using ChainShape
        for (int vertexIdx = 0; vertexIdx < polyVertices.length; vertexIdx += 2) {
            // x-coordinate
            vertices[vertexIdx] = offsetX + polyVertices[vertexIdx] * GdxGame.UNIT_SCALE * scaling.x;
            // y-coordinate
            vertices[vertexIdx + 1] = offsetY + polyVertices[vertexIdx + 1] * GdxGame.UNIT_SCALE * scaling.y;
        }

        ChainShape shape = new ChainShape(); // this works best for complex custom polygon
        if (mapObject instanceof PolygonMapObject) {
            shape.createLoop(vertices); // connect the vertices to make the shape
        } else { // PolylineMapObject
            shape.createChain(vertices);
        }
        return fixtureDefOfMapObjectAndShape(mapObject, shape);
    }

    // this method helps convert our shape data into a fixture definition which is the required return type
    private static FixtureDef fixtureDefOfMapObjectAndShape(MapObject mapObject, Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape; // this is the main one

        // these are extra properties that are set to default values for now
        fixtureDef.friction = mapObject.getProperties().get("friction", 0f, Float.class); // values between 0-1, no friction means 0
        fixtureDef.restitution = mapObject.getProperties().get("restitution", 0f, Float.class); // bounciness of objects, between 0-1, so if 1 then perfect maintenance of energy and when ball dropped it returns to original height. ideal collisions
        fixtureDef.density = mapObject.getProperties().get("density", 0f, Float.class); // density helps with forces and interactions between physics objects. Derive required value from map object directly, whose properties are set in Tiled
        fixtureDef.isSensor = mapObject.getProperties().get("sensor", false, Boolean.class); // if a fixture is a sensor it does not trigger collision detection, but it does run the collision events/contact events
        return fixtureDef;
    }
}
