/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.terrain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Tile;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.utils.LightSerializationUtils;
import de.projectsc.core.entities.Entity;

/**
 * Loader for a terrain map with its static lights and objects.
 * 
 * @author Josch Bosch
 */
@SuppressWarnings("unused")
public final class TerrainLoader {

    private static final String POSITION = "position";

    private static final String SCALE = "scale";

    private static final String MODEL = "model";

    private static final String TEXTURE = "texture";

    private static final String TYPE = "type";

    private static final String ID = "id";

    private static final String ROTATION = "rotation";

    private static final String STATIC_OBJECTS = "staticObjects";

    private static final String STATIC_LIGHTS = "staticLights";

    private static final String B_TEXTURE = "BTexture";

    private static final String G_TEXTURE = "GTexture";

    private static final String R_TEXTURE = "RTexture";

    private static final String BG_TEXTURE = "bgTexture";

    private static final String TERRAIN_HEIGHT = "terrainHeight";

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
            Tile[][] map = new Tile[mapSize][mapSize];
            for (int i = 0; i < mapSize; i++) {
                for (int j = 0; j < mapSize; j++) {
                    map[i][j] = null;
                }
            }

            ArrayNode terrain = (ArrayNode) tree.get(TERRAIN_DATA);

            for (JsonNode terrainFieldObject : terrain) {
                ObjectNode terrainField = (ObjectNode) terrainFieldObject;
                Integer[] position = mapper.readValue(terrainField.get(POSITION), new Integer[2].getClass());
                map[position[0]][position[1]] =
                    new Tile(new Vector2f(position[0], position[1]), mapper.readValue(terrainField.get(TERRAIN_HEIGHT), Byte.class),
                        mapper.readValue(terrainField.get("walkable"), Byte.class), mapper.readValue(terrainField.get(TYPE),
                            Byte.class));
            }

            String bgTexture = mapper.readValue(tree.get(BG_TEXTURE), String.class);
            String rTexture = mapper.readValue(tree.get(R_TEXTURE), String.class);
            String gTexture = mapper.readValue(tree.get(G_TEXTURE), String.class);
            String bTexture = mapper.readValue(tree.get(B_TEXTURE), String.class);

            ObjectNode lightsNode = (ObjectNode) tree.get(STATIC_LIGHTS);

            List<Light> staticLights = LightSerializationUtils.deserializeLights(mapper, lightsNode);

            ArrayNode staticObjectsNode = (ArrayNode) tree.get(STATIC_OBJECTS);
            Map<Integer, Entity> staticObjects = new TreeMap<>();
            if (staticObjectsNode != null) {
                for (JsonNode object : staticObjectsNode) {
                    ObjectNode objectNode = (ObjectNode) object;
                    Float[] positionValues = mapper.readValue(objectNode.get(POSITION), new Float[3].getClass());
                    Vector3f position = new Vector3f(positionValues[0], positionValues[1], positionValues[2]);
                    Float[] rotationValues = mapper.readValue(objectNode.get(ROTATION), new Float[3].getClass());
                    float yRot = rotationValues[1];
                    Vector3f rotation = new Vector3f(rotationValues[0], yRot, rotationValues[2]);

                    // staticObjects.put(objectNode.get(ID).asInt(), new Entity(10000));
                }
            }
            return null;
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
        List<Object> terrainList = new LinkedList<>();
        for (int i = 0; i < terrain.getMapSize(); i++) {
            for (int j = 0; j < terrain.getMapSize(); j++) {
                if (terrain.getTerrain()[i][j] != null) {
                    Map<String, Object> terrainData = new HashMap<>();
                    Tile current = terrain.getTerrain()[i][j];
                    terrainData.put(POSITION, new int[] { (int) current.getCoordinates().x, (int) current.getCoordinates().y });
                    terrainData.put(TERRAIN_HEIGHT, current.getHeight());
                    terrainData.put("walkable", current.getWalkAble());
                    terrainData.put(TYPE, current.getType());
                    terrainList.add(terrainData);
                }
            }
        }

        map.put(TERRAIN_DATA, terrainList);
        map.put(SIZE, terrain.getMapSize());
        // Map<String, Map<String, Float[]>> lights =
        // Serialization.createSerializableMap(terrain.getStaticLights());
        // map.put(STATIC_LIGHTS, lights);

        // Map<Integer, Entity> staticObjects = terrain.getStaticObjects();
        // List<Map<String, Object>> entities = new LinkedList<Map<String, Object>>();
        // for (Entity e : staticObjects.values()) {
        // Map<String, Object> newEntity = new HashMap<String, Object>();
        // newEntity.put(ID, e.getID());
        // newEntity.put(TYPE, e.getType().toString());
        // newEntity.put(MODEL, e.getModel());
        // newEntity.put(TEXTURE, e.getTexture());
        // newEntity.put(SCALE, e.getScale());
        // newEntity.put(POSITION, new Float[] { e.getPosition().x, e.getPosition().y,
        // e.getPosition().z });
        // newEntity.put(ROTATION, new Float[] { e.getRotX(), e.getRotY(), e.getRotZ() });
        // entities.add(newEntity);
        // }
        // map.put(STATIC_OBJECTS, entities);
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
                if (terrain.getTerrain()[i][j] != null) {
                    switch (terrain.getTerrain()[i][j].getType()) {
                    case 0:
                        g.setColor(Color.RED);
                        break;
                    case 1:
                        g.setColor(Color.GREEN);
                        break;
                    case 2:
                        g.setColor(Color.BLUE);
                        break;
                    default:
                        break;
                    }
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
