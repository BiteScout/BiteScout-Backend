package com.bitescout.app.notificationservice.restaurant.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // Parse JSON
        JsonNode node = parser.getCodec().readTree(parser);

        // Expecting "longitude" and "latitude" fields
        JsonNode longitudeNode = node.get("longitude");
        JsonNode latitudeNode = node.get("latitude");

        // Validate fields
        if (longitudeNode == null || latitudeNode == null) {
            throw new IllegalArgumentException("Invalid location format. Expected 'longitude' and 'latitude' fields.");
        }

        // Extract coordinates
        double longitude = longitudeNode.asDouble();
        double latitude = latitudeNode.asDouble();

        // Create and return Point
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
