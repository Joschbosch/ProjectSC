/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.objects.terrain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.Tile;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.core.terrain.TerrainLoader;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.textures.TerrainTexture;
import de.projectsc.modes.client.gui.textures.TerrainTexturePack;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Representation of the terrain.
 * 
 * @author Josch Bosch
 */
public class TerrainModel {

    private static final Log LOGGER = LogFactory.getLog(TerrainModel.class);

    private static RawModel model;

    private final float xCoord;

    private final float zCoord;

    private final TerrainTexturePack texture;

    private final TerrainTexture blendMap;

    private final Tile[][] tiles;

    private final Terrain terrain;

    public TerrainModel(Terrain terrain) {
        this.terrain = terrain;
        this.tiles = terrain.getTerrain();
        this.xCoord = terrain.getWorldPositionX();
        this.zCoord = terrain.getWorldPositionZ();
        TerrainTexture backgroundTex = new TerrainTexture(Loader.loadTexture(terrain.getBGTexture()));
        TerrainTexture rTex = new TerrainTexture(Loader.loadTexture(terrain.getRTexture()));
        TerrainTexture gTex = new TerrainTexture(Loader.loadTexture(terrain.getGTexture()));
        TerrainTexture bTex = new TerrainTexture(Loader.loadTexture(terrain.getBTexture()));
        texture = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        blendMap = new TerrainTexture(Loader.loadTexture(TerrainLoader.createBlendMap(terrain)));
        if (model == null) {
            model = generateTerrainModel();
        }
    }

    private RawModel generateTerrainModel() {

        LOGGER.debug("Start generating terrain.");
        int vertCountX = Terrain.TERRAIN_CHUNK_SIZE;
        int count = vertCountX * vertCountX;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (vertCountX - 1) * (vertCountX - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < vertCountX; i++) {
            for (int j = 0; j < vertCountX; j++) {
                vertices[vertexPointer * 3] = j / ((float) vertCountX - 1) * vertCountX * Terrain.TERRAIN_TILE_SIZE;
                float height = tiles[i][j] != null ? tiles[i][j].getHeight() : 0;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = i / ((float) vertCountX - 1) * vertCountX * Terrain.TERRAIN_TILE_SIZE;
                Vector3f normal = calculateNormal(j, i);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = j / ((float) vertCountX - 1);
                textureCoords[vertexPointer * 2 + 1] = i / ((float) vertCountX - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < vertCountX - 1; gz++) {
            for (int gx = 0; gx < vertCountX - 1; gx++) {
                int topLeft = (gz * vertCountX) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * vertCountX) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        LOGGER.debug("Terrain generated");
        return Loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int z) {
        float heightL = terrain.getHeightOfTerrain(x - 1, z) * Terrain.HEIGHT_TILE_SIZE;
        float heightR = terrain.getHeightOfTerrain(x + 1, z) * Terrain.HEIGHT_TILE_SIZE;
        float heightD = terrain.getHeightOfTerrain(x - 1, z - 1) * Terrain.HEIGHT_TILE_SIZE;
        float heightU = terrain.getHeightOfTerrain(x, z + 1) * Terrain.HEIGHT_TILE_SIZE;
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    public float getX() {
        return xCoord;
    }

    public float getZ() {
        return zCoord;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexture() {
        return texture;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public Terrain getTerrain() {
        return terrain;
    }

}
