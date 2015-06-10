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
public final class TileSetStore {

    private static final Log LOGGER = LogFactory.getLog(TileSetStore.class);

    private static final Map<Integer, Integer> TILE_IDS = new HashMap<>();

    private TileSetStore() {}

    /**
     * Loads the complete tileset.
     */
    public static void loadTileSet() {
        try {
            BufferedImage tileSets =
                ImageIO.read(TileSetStore.class.getResource("/graphics/DungeonCrawl_ProjectUtumnoTileset.png"));
            int index = 0;
            int i = 0;
            int j = 0;
            final int tileSize = 32;
            while (true) {
                if (i * (tileSize + 1) >= tileSets.getWidth()) {
                    j++;
                    i = 0;
                }
                if (j * (tileSize + 1) > tileSets.getHeight()) {
                    break;
                }
                BufferedImage currentTile = tileSets.getSubimage(i * tileSize, j * tileSize, tileSize, tileSize);
                TILE_IDS.put(index++, GraphicsUtils.loadTexture(currentTile));
                currentTile = null;
                i++;
            }
        } catch (IOException e) {
            LOGGER.error("Could not load tiles: ", e);
        }

    }
}
