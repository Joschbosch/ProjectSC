/*
 * Copyright (C) 2015 
 */

package de.projectsc.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;

public class TerrainLoader {

    private static final Log LOGGER = LogFactory.getLog(TerrainLoader.class);

    public static Terrain loadTerrain(String filename) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode tree = mapper.readTree(new File(filename));
            int mapSize = tree.get("size").asInt();
            int[][] map = mapper.readValue(tree.get("terrainData"), new int[mapSize][mapSize].getClass());
            float[][] heights = mapper.readValue(tree.get("terrainheights"), new float[mapSize][mapSize].getClass());
            String bgTexture = mapper.readValue(tree.get("bgTexture"), String.class);
            String rTexture = mapper.readValue(tree.get("RTexture"), String.class);
            String gTexture = mapper.readValue(tree.get("GTexture"), String.class);
            String bTexture = mapper.readValue(tree.get("BTexture"), String.class);
            Map<String, Map<String, float[]>> lights =
                mapper.readValue("staticLights", new HashMap<String, Map<String, float[]>>().getClass());
            List<Light> staticLights = new LinkedList<>();
            for (String light : lights.keySet()) {
                Map<String, float[]> current = lights.get(light);
                Vector3f position = new Vector3f(current.get("position")[0], current.get("position")[1], current.get("position")[2]);
                Vector3f color = new Vector3f(current.get("color")[0], current.get("color")[1], current.get("color")[2]);
                Vector3f attenuation =
                    new Vector3f(current.get("attenuation")[0], current.get("attenuation")[1], current.get("attenuation")[2]);
                Light l = new Light(position, color, attenuation, light);
                staticLights.add(l);
            }

            return new Terrain(map, heights, bgTexture, rTexture, gTexture, bTexture, staticLights);
        } catch (IOException e) {
            LOGGER.error("Error loading map: ", e);
        }
        return null;
    }

    public static void storeTerrain(Terrain terrain, String file) {
        Map<String, Object> mapFile = new HashMap<>();
        mapFile.put("terrainData", terrain.getTerrain());
        mapFile.put("terrainheights", terrain.getHeights());
        mapFile.put("size", terrain.getMapSize());
        mapFile.put("bgTexture", terrain.getBgTexture());
        mapFile.put("RTexture", terrain.getrTexture());
        mapFile.put("GTexture", terrain.getgTexture());
        mapFile.put("BTexture", terrain.getbTexture());
        List<Light> staticLights = terrain.getStaticLights();
        Map<String, Map<String, float[]>> lights = new HashMap<>();
        for (Light l : staticLights) {
            Map<String, float[]> light = new HashMap<>();
            light.put("attenuation", new float[] { l.getAttenuation().x, l.getAttenuation().y, l.getAttenuation().z });
            light.put("color", new float[] { l.getColor().x, l.getColor().y, l.getColor().z });
            light.put("position", new float[] { l.getPosition().x, l.getPosition().y, l.getPosition().z });
            lights.put(l.getName(), light);
        }
        mapFile.put("staticLights", lights);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writer().writeValue(new File(file), mapFile);
        } catch (IOException e) {
            LOGGER.error(e.getStackTrace());
        }
    }

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
