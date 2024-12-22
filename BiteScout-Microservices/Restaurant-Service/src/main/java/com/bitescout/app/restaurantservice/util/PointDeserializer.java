package com.bitescout.app.restaurantservice.util;

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
        JsonNode node = parser.getCodec().readTree(parser);
        JsonNode coordinatesNode = node.get("coordinates");

        if (coordinatesNode == null || !coordinatesNode.isArray() || coordinatesNode.size() != 2) {
            throw new IllegalArgumentException("Invalid 'coordinates' field in JSON. It should be an array with two elements.");
        }

        double longitude = coordinatesNode.get(0).asDouble();
        double latitude = coordinatesNode.get(1).asDouble();

        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
