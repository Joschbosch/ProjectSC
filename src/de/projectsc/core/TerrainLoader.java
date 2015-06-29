/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;

/**
 * Loader for a terrain map with its static lights and objects.
 * 
 * @author Josch Bosch
 */
public final class TerrainLoader {

    private static final String SCALE = "scale";

    private static final String MODEL = "model";

    private static final String TEXTURE = "texture";

    private static final String TYPE = "type";

    private static final String ID = "id";

    private static final String ROTATION = "rotation";

    private static final String STATIC_OBJECTS = "staticObjects";

    private static final String ATTENUATION = "attenuation";

    private static final String COLOR = "color";

    private static final String POSITION = "position";

    private static final String STATIC_LIGHTS = "staticLights";

    private static final String B_TEXTURE = "BTexture";

    private static final String G_TEXTURE = "GTexture";

    private static final String R_TEXTURE = "RTexture";

    private static final String BG_TEXTURE = "bgTexture";

    private static final String TERRAIN_HEIGHTS = "terrainHeights";

    private static final String TERRAIN_DATA = "terrainData";

    private static final String SIZE = "size";

    private static final Log LOGGER = LogFactory.getLog(TerrainLoader.class);

    private TerrainLoader() {}

    /**
     * Loads the terrain with the given filename.
     * 
     * @param filename to load
     * @return all terrain data.
     */
    public static Terrain loadTerrain(String filename) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode tree = mapper.readTree(new File(filename));
            int mapSize = tree.get(SIZE).asInt();
            int[][] map = mapper.readValue(tree.get(TERRAIN_DATA), new int[mapSize][mapSize].getClass());
            float[][] heights = mapper.readValue(tree.get(TERRAIN_HEIGHTS), new float[mapSize][mapSize].getClass());
            String bgTexture = mapper.readValue(tree.get(BG_TEXTURE), String.class);
            String rTexture = mapper.readValue(tree.get(R_TEXTURE), String.class);
            String gTexture = mapper.readValue(tree.get(G_TEXTURE), String.class);
            String bTexture = mapper.readValue(tree.get(B_TEXTURE), String.class);

            ObjectNode lightsNode = (ObjectNode) tree.get(STATIC_LIGHTS);
            Iterator<String> lightsIterator = lightsNode.getFieldNames();
            List<Light> staticLights = new ArrayList<Light>();
            while (lightsIterator.hasNext()) {
                String light = lightsIterator.next();
                Vector3f[] attributes = new Vector3f[3];
                int i = 0;
                for (String attributeName : new String[] { POSITION, COLOR, ATTENUATION }) {
                    Float[] attribute = mapper.readValue(lightsNode.get(light).get(attributeName), new Float[3].getClass());
                    attributes[i++] = new Vector3f(attribute[0], attribute[1], attribute[2]);

                }
                Light l = new Light(attributes[0], attributes[1], attributes[2], light);
                staticLights.add(l);
            }

            ArrayNode staticObjectsNode = (ArrayNode) tree.get(STATIC_OBJECTS);
            Map<Integer, WorldEntity> staticObjects = new TreeMap<Integer, WorldEntity>();
            if (staticObjectsNode != null) {
                for (JsonNode object : staticObjectsNode) {
                    ObjectNode objectNode = (ObjectNode) object;
                    Float[] positionValues = mapper.readValue(objectNode.get(POSITION), new Float[3].getClass());
                    Vector3f position = new Vector3f(positionValues[0], positionValues[1], positionValues[2]);
                    Float[] rotationValues = mapper.readValue(objectNode.get(ROTATION), new Float[3].getClass());
                    Vector3f rotation = new Vector3f(rotationValues[0], rotationValues[1], rotationValues[2]);
                    System.out.println("P1" + rotation.y);
                    WorldEntity e =
                        new WorldEntity(objectNode.get(ID).asInt(), EntityType.valueOf(objectNode.get(TYPE).asText()), objectNode.get(
                            MODEL).asText(), objectNode.get(TEXTURE).asText(), position, rotation, (float) objectNode.get(SCALE)
                            .asDouble());
                    // e.setRotY(0);
                    staticObjects.put(objectNode.get(ID).asInt(), e);
                }
            }
            return new Terrain(map, heights, bgTexture, rTexture, gTexture, bTexture, staticLights, staticObjects);
        } catch (IOException e) {
            LOGGER.error("Error loading map: ", e);
        }
        return null;
    }

    /**
     * 
     * Saves the given terrain in the given file.
     * 
     * @param terrain to save
     * @param file to save to
     */
    public static void storeTerrain(Terrain terrain, String file) {
        File target = new File(file);
        Map<String, Object> map = new HashMap<>();
        map.put(TERRAIN_DATA, terrain.getTerrain());
        map.put(TERRAIN_HEIGHTS, terrain.getHeights());
        map.put(SIZE, terrain.getMapSize());
        map.put(BG_TEXTURE, terrain.getBgTexture());
        map.put(R_TEXTURE, terrain.getRTexture());
        map.put(G_TEXTURE, terrain.getGTexture());
        map.put(B_TEXTURE, terrain.getBTexture());
        List<Light> staticLights = terrain.getStaticLights();
        Map<String, Map<String, Float[]>> lights = new HashMap<>();
        for (Light l : staticLights) {
            Map<String, Float[]> light = new HashMap<>();
            light.put(ATTENUATION, new Float[] { l.getAttenuation().x, l.getAttenuation().y, l.getAttenuation().z });
            light.put(COLOR, new Float[] { l.getColor().x, l.getColor().y, l.getColor().z });
            light.put(POSITION, new Float[] { l.getPosition().x, l.getPosition().y, l.getPosition().z });
            lights.put(l.getName(), light);
        }
        map.put(STATIC_LIGHTS, lights);

        Map<Integer, WorldEntity> staticObjects = terrain.getStaticObjects();
        List<Map<String, Object>> entities = new LinkedList<Map<String, Object>>();
        for (WorldEntity e : staticObjects.values()) {
            Map<String, Object> newEntity = new HashMap<String, Object>();
            newEntity.put(ID, e.getID());
            newEntity.put(TYPE, e.getType().toString());
            newEntity.put(MODEL, e.getModel());
            newEntity.put(TEXTURE, e.getTexture());
            newEntity.put(SCALE, e.getScale());
            newEntity.put(POSITION, new Float[] { e.getPosition().x, e.getPosition().y, e.getPosition().z });
            newEntity.put(ROTATION, new Float[] { e.getRotX(), e.getRotY(), e.getRotZ() });
            entities.add(newEntity);
        }
        map.put(STATIC_OBJECTS, entities);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(target, map);
        } catch (IOException e) {
            LOGGER.error(e.getStackTrace());
        }
    }

    /**
     * 
     * Creates the blend map for the given terrain.
     * 
     * @param terrain used for the blendmap
     * @return file to the blend map created
     */
    public static File createBlendMap(Terrain terrain) {
        BufferedImage img = new BufferedImage(terrain.getMapSize(), terrain.getMapSize(), BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        for (int i = 0; i < terrain.getMapSize(); i++) {
            for (int j = 0; j < terrain.getMapSize(); j++) {
                if (i > terrain.getMapSize() / 2) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawLine(i, j, i, j);
            }
        }

        File blendmap = null;
        try {
            blendmap = new File("blendmap.png");
            if (!blendmap.exists()) {
                blendmap.createNewFile();
            }
            ImageIO.write(img, "PNG", blendmap);
        } catch (IOException e) {
        }
        return blendmap;
    }
}
