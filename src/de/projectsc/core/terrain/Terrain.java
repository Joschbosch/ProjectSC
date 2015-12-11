/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.terrain;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.graph.GraphEdge;
import de.projectsc.core.data.physics.BoundingBox;
import de.projectsc.core.data.physics.Tile;
import de.projectsc.core.utils.Maths;

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
     * One terrain has always the chunk size of tiles.
     */
    public static final int TERRAIN_CHUNK_SIZE = 32;

    /**
     * Tile size height.
     */
    public static final float HEIGHT_TILE_SIZE = 8.0f;

    private final int mapSize;

    private final float worldPositionX;

    private final float worldPositionZ;

    private final Tile[][] tiles;

    private BoundingBox mapBox;

    private float worldPositionXMax;

    private float worldPositionZMax;

    private final int tileCoordinateX;

    private final int tileCoordinateZ;

    private final String bgTexture;

    private final String rTexture;

    private final String gTexture;

    private final String bTexture;

    public Terrain(int x, int z, String bgTexture, String rTexture, String gTexture,
        String bTexture) {
        this.tiles = new Tile[TERRAIN_CHUNK_SIZE][TERRAIN_CHUNK_SIZE];
        for (int i = 0; i < TERRAIN_CHUNK_SIZE; i++) {
            for (int j = 0; j < TERRAIN_CHUNK_SIZE; j++) {
                tiles[i][j] =
                    new Tile(new Vector2f(x + i * TERRAIN_TILE_SIZE, z + j * TERRAIN_TILE_SIZE), (byte) 0, Tile.WALKABLE, (byte) 0);
            }
        }
        this.mapSize = TERRAIN_CHUNK_SIZE;
        this.setMapBox(calculateMapBox());
        this.tileCoordinateX = x;
        this.tileCoordinateZ = x;
        this.worldPositionX = x * TERRAIN_CHUNK_SIZE * TERRAIN_TILE_SIZE;
        this.worldPositionZ = z * TERRAIN_CHUNK_SIZE * TERRAIN_TILE_SIZE;
        this.setWorldPositionXMax(x + TERRAIN_CHUNK_SIZE * TERRAIN_TILE_SIZE);
        this.setWorldPositionZMax(z + TERRAIN_CHUNK_SIZE * TERRAIN_TILE_SIZE);
        this.bgTexture = bgTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
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
     * Return height of coordinate [x,z].
     * 
     * @param x coordinate
     * @param z coordinate
     * @return height
     */
    public byte getHeightOfTerrain(int x, int z) {
        if (x >= 0 && x < tiles.length && z >= 0 && z < tiles[0].length && tiles[x][z] != null) {
            return tiles[x][z].getHeight();
        }
        return 0;
    }

    /**
     * Calculates the height of the Terrain at world position (x,z).
     * 
     * @param xWorld coordinate
     * @param zWorld coordinate
     * @return height
     */
    public float getHeightOfTerrain(float xWorld, float zWorld) {
        float terrainX = xWorld - this.tileCoordinateX;
        float terrainZ = zWorld - this.tileCoordinateX;
        float gridSize = Terrain.TERRAIN_CHUNK_SIZE / ((float) Terrain.TERRAIN_CHUNK_SIZE - 1);
        int gridX = (int) Math.floor(terrainX / gridSize);
        int gridZ = (int) Math.floor(terrainZ / gridSize);
        if (gridX >= Terrain.TERRAIN_CHUNK_SIZE - 1 || gridZ >= Terrain.TERRAIN_CHUNK_SIZE - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }
        float xCoordPlayer = (terrainX % gridSize) / gridSize;
        float zCoordPlayer = (terrainZ % gridSize) / gridSize;

        float answer = 0.0f;
        if (xCoordPlayer <= (1 - zCoordPlayer)) {
            answer =
                Maths.barryCentric(new Vector3f(0, getHeightOfTerrain(gridX, gridZ) * Terrain.HEIGHT_TILE_SIZE, 0), new Vector3f(1,
                    getHeightOfTerrain(gridX + 1, gridZ) * Terrain.HEIGHT_TILE_SIZE, 0), new Vector3f(0,
                    getHeightOfTerrain(gridX, gridZ + 1) * Terrain.HEIGHT_TILE_SIZE, 1),
                    new Vector2f(xCoordPlayer, zCoordPlayer));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, getHeightOfTerrain(gridX + 1, gridZ) * Terrain.HEIGHT_TILE_SIZE, 0),
                new Vector3f(1,
                    getHeightOfTerrain(gridX + 1, gridZ + 1) * Terrain.HEIGHT_TILE_SIZE, 1),
                new Vector3f(0,
                    getHeightOfTerrain(gridX, gridZ + 1) * Terrain.HEIGHT_TILE_SIZE, 1),
                new Vector2f(xCoordPlayer, zCoordPlayer));
        }
        return answer;
    }

    /**
     * Set all static object to not walkable on map.
     */
    public void makeStaticObjectsNotWalkable() {
        // for (WorldEntity e : staticObjects.values()) {
        // markEntityPosition(e, Tile.NOT_WALKABLE);
        // }
    }

    // /**
    // * Set all entities to *mark* on walkable map.
    // *
    // * @param mark to set to
    // * @param list of entities
    // */
    // public void markEntitiyObjects(byte mark, List<Entity> list) {
    // for (Entity e : list) {
    // markEntityPosition(e, mark);
    // }
    // }
    //
    // /**
    // * set entitiy to mark.
    // *
    // * @param e entity
    // * @param mark to set to
    // */
    // public void markEntityPosition(Entity e, byte mark) {
    // if (e.getBoundingBox() != null) {
    // Vector3f realMinPos = Vector3f.add(e.getBoundingBox().getMin(), e.getPosition(), null);
    // Vector3f realMaxPos = Vector3f.add(e.getBoundingBox().getMax(), e.getPosition(), null);
    // if (realMinPos.x > 0 && realMinPos.z > 0) {
    // realMinPos = (Vector3f) realMinPos.scale(1.0f / Terrain.TERRAIN_TILE_SIZE);
    // realMaxPos = (Vector3f) realMaxPos.scale(1.0f / Terrain.TERRAIN_TILE_SIZE);
    // for (int i = (int) realMinPos.x; i <= (int) realMaxPos.x; i++) {
    // for (int j = (int) realMinPos.z; j <= (int) realMaxPos.z; j++) {
    // if (tiles[i][j] != null) {
    // tiles[i][j].setWalkable(mark);
    //
    // }
    // }
    // }
    // }
    // }
    // }

    public int getMapSize() {
        return mapSize;
    }

    public Tile[][] getTerrain() {
        return tiles;
    }

    public BoundingBox getMapBoundingBox() {
        return mapBox;
    }

    public void setMapBox(BoundingBox mapBox) {
        this.mapBox = mapBox;
    }

    public float getWorldPositionX() {
        return worldPositionX;
    }

    public float getWorldPositionZ() {
        return worldPositionZ;
    }

    public float getWorldPositionXMax() {
        return worldPositionXMax;
    }

    public void setWorldPositionXMax(float worldPositionXMax) {
        this.worldPositionXMax = worldPositionXMax;
    }

    public float getWorldPositionZMax() {
        return worldPositionZMax;
    }

    public void setWorldPositionZMax(float worldPositionZMax) {
        this.worldPositionZMax = worldPositionZMax;
    }

    public int getTileCoordinateX() {
        return tileCoordinateX;
    }

    public int getTileCoordinateZ() {
        return tileCoordinateZ;
    }

    public String getBGTexture() {
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

}
