/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
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

    private AxisAlignedBoundingBox mapBox;

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

    private AxisAlignedBoundingBox calculateMapBox() {
        Vector3f minimum = new Vector3f(0, 0, 0);
        Vector3f maximum =
            new Vector3f(mapSize * Terrain.TERRAIN_TILE_SIZE, mapSize * Terrain.TERRAIN_TILE_SIZE, mapSize * Terrain.TERRAIN_TILE_SIZE);
        return new AxisAlignedBoundingBox(minimum, maximum);
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
        return 0;
    }

    /**
     * Return height of coordinate [x,z].
     * 
     * @param x coordinate
     * @param z coordinate
     * @return height
     */
    public byte getHeightOfTerrain(int x, int z) {
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

    public AxisAlignedBoundingBox getMapBoundingBox() {
        return mapBox;
    }

    public void setMapBox(AxisAlignedBoundingBox mapBox) {
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
