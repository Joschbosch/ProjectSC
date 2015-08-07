/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.GraphEdge;

/**
 * Holds all information about the static terrain.
 * 
 * @author Josch Bosch
 */
public class Terrain {

    /**
     * Factor for a terrain offset. (1 tile has offset [0, TERRAIN_TILE_SIZE])
     */
    public static final float TERRAIN_TILE_SIZE = 4.0f;

    /**
     * Tile size height.
     */
    public static final float HEIGHT_TILE_SIZE = 8.0f;

    private final int mapSize;

    private final Tile[][] tiles;

    private final String bgTexture;

    private final String rTexture;

    private final String gTexture;

    private final String bTexture;

    private List<Light> staticLights;

    private Map<Integer, WorldEntity> staticObjects;

    private BoundingBox mapBox;

    public Terrain(Tile[][] tiles, String bgTexture, String rTexture, String gTexture,
        String bTexture, List<Light> lights, Map<Integer, WorldEntity> staticObjects) {
        this.tiles = tiles;
        this.mapSize = tiles.length;
        this.bgTexture = bgTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
        this.setStaticLights(lights);
        this.staticObjects = staticObjects;
        this.setMapBox(calculateMapBox());
    }

    private BoundingBox calculateMapBox() {
        Vector3f minimum = new Vector3f(0, 0, 0);
        Vector3f maximum =
            new Vector3f(mapSize * Terrain.TERRAIN_TILE_SIZE, mapSize * Terrain.TERRAIN_TILE_SIZE, mapSize * Terrain.TERRAIN_TILE_SIZE);

        return new BoundingBox(minimum, maximum);
    }

    /**
     * Calculate normal vector of tile [x,y].
     * 
     * @param x coordinate.
     * @param z coordinate.
     * @return normal vector
     */
    public Vector3f calculateNormal(int x, int z) {
        float heightL = getHeight(x - 1, z);
        float heightR = getHeight(x + 1, z);
        float heightD = getHeight(x - 1, z - 1);
        float heightU = getHeight(x, z + 1);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    /**
     * Return height of coordinate [x,z].
     * 
     * @param x coordinate
     * @param z coordinate
     * @return height
     */
    public byte getHeight(int x, int z) {
        if (x >= 0 && x < tiles.length && z >= 0 && z < tiles[0].length && tiles[x][z] != null) {
            return tiles[x][z].getHeight();
        }
        return 0;
        // if (x < 0 || x >= map.getHeight() || z < 0 || z >= map.getWidth()) {
        // return 0;
        // }
        // float height = map.getRGB(x, z);
        // height += MAX_PIXEL_COLOR / 2f;
        // height /= MAX_PIXEL_COLOR / 2f;
        // height *= MAXIMUM_HEIGHT;
        // return height;
    }

    /**
     * Build up neighborhood for the loaded terrain.
     */
    public void buildNeighborhood() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                List<GraphEdge> neighbors = new LinkedList<>();
                if (tiles[i][j] != null) {
                    for (int k = 0 - 1; k < 2; k++) {
                        for (int l = 0 - 1; l < 2; l++) {
                            if (!(l == 0 && k == 0)) {
                                GraphEdge neighbor = getNeightborAt(tiles[i][j], i + k, j + l);
                                if (neighbor != null) {
                                    neighbors.add(neighbor);
                                }
                            }
                        }
                    }
                    tiles[i][j].setNeighbors(neighbors);
                }
            }
        }
    }

    private GraphEdge getNeightborAt(Tile source, int i, int j) {
        GraphEdge result = new GraphEdge(source, null, 0 - 1);
        if (i >= 0 && j >= 0 && i < mapSize && j < mapSize) {
            if (tiles[i][j] != null) {
                result.setTarget(tiles[i][j]);
                result.setCost(
                    (float) Math.sqrt((Math.pow(i - source.getCoordinates().x, 2) + Math.pow(j - source.getCoordinates().y, 2))));
                return result;
            }
        }
        return null;
    }

    /**
     * Set all static object to not walkable on map.
     */
    public void makeStaticObjectsNotWalkable() {
        for (WorldEntity e : staticObjects.values()) {
            markEntityPosition(e, Tile.NOT_WALKABLE);
        }
    }

    /**
     * Set all entities to *mark* on walkable map.
     * 
     * @param mark to set to
     * @param list of entities
     */
    public void markEntitiyObjects(byte mark, List<WorldEntity> list) {
        for (WorldEntity e : list) {
            markEntityPosition(e, mark);
        }
    }

    /**
     * set entitiy to mark.
     * 
     * @param e entity
     * @param mark to set to
     */
    public void markEntityPosition(WorldEntity e, byte mark) {
        if (e.getBoundingBox() != null) {
            Vector3f realMinPos = Vector3f.add(e.getBoundingBox().getMin(), e.getPosition(), null);
            Vector3f realMaxPos = Vector3f.add(e.getBoundingBox().getMax(), e.getPosition(), null);
            if (realMinPos.x > 0 && realMinPos.z > 0) {
                realMinPos = (Vector3f) realMinPos.scale(1.0f / Terrain.TERRAIN_TILE_SIZE);
                realMaxPos = (Vector3f) realMaxPos.scale(1.0f / Terrain.TERRAIN_TILE_SIZE);
                for (int i = (int) realMinPos.x; i <= (int) realMaxPos.x; i++) {
                    for (int j = (int) realMinPos.z; j <= (int) realMaxPos.z; j++) {
                        if (tiles[i][j] != null) {
                            tiles[i][j].setWalkable(mark);

                        }
                    }
                }
            }
        }
    }

    public int getMapSize() {
        return mapSize;
    }

    public Tile[][] getTerrain() {
        return tiles;
    }

    public String getBgTexture() {
        return bgTexture;
    }

    public String getRTexture() {
        return rTexture;
    }

    public String getGTexture() {
        return gTexture;
    }

    public String getBTexture() {
        return bTexture;
    }

    public List<Light> getStaticLights() {
        return staticLights;
    }

    public void setStaticLights(List<Light> staticLights) {
        this.staticLights = staticLights;
    }

    public Map<Integer, WorldEntity> getStaticObjects() {
        return staticObjects;
    }

    public void setStaticObjects(Map<Integer, WorldEntity> staticObjects) {
        this.staticObjects = staticObjects;
    }

    public BoundingBox getMapBoundingBox() {
        return mapBox;
    }

    public void setMapBox(BoundingBox mapBox) {
        this.mapBox = mapBox;
    }

}
