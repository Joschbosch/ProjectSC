/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.gui.GraphicsUtils;

/**
 * Stores all tiles used in the game.
 * 
 * @author Josch Bosch
 */
public final class TileMap {

    private static final Log LOGGER = LogFactory.getLog(TileMap.class);

    private static final Map<Integer, Float[]> TILE_COORDINATES = new HashMap<>();

    private static int tilesetID;

    private static float tileVertexSize[];

    public static final int TILE_SIZE = 32;

    private TileMap() {}

    /**
     * Loads the complete tileset.
     */
    public static void loadTileSet() {
        try {
            BufferedImage tileSets =
                ImageIO.read(TileMap.class.getResource("/graphics/DungeonCrawl_ProjectUtumnoTileset.png"));
            tilesetID = GraphicsUtils.loadTexture(tileSets);
            int index = 0;
            int i = 0;
            int j = 0;
            tileVertexSize =
                new float[] { (float) TILE_SIZE / (float) tileSets.getWidth(), (float) TILE_SIZE / (float) tileSets.getHeight() };
            while (true) {
                if (i * (TILE_SIZE + 1) >= tileSets.getWidth()) {
                    j++;
                    i = 0;
                }
                if (j * (TILE_SIZE + 1) > tileSets.getHeight()) {
                    break;
                }
                TILE_COORDINATES.put(index++, new Float[] { i * tileVertexSize[0], j * tileVertexSize[1] });
                i++;
            }
        } catch (IOException e) {
            LOGGER.error("Could not load tiles: ", e);
        }

    }

    public static int getTilesetID() {
        return tilesetID;
    }

    public static Float[] getTextureCoordinates(Integer tileID) {
        return TILE_COORDINATES.get(tileID);
    }

    public static float[] getTileVertexSize() {
        return tileVertexSize;
    }

}
