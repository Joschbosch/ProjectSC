/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.terrain;

import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.render.Loader;
import de.projectsc.gui.textures.TerrainTexture;
import de.projectsc.gui.textures.TerrainTexturePack;

public class Terrain {

    private int VERTEX_COUNT = 128;

    private float SIZE = 800;

    private float x;

    private float z;

    private RawModel model;

    private TerrainTexturePack texture;

    private TerrainTexture blendMap;

    public Terrain(float x, float z, TerrainTexturePack texture, TerrainTexture blendMap, Loader loader) {
        super();
        this.x = x * SIZE;
        this.z = z * SIZE;
        this.texture = texture;
        this.blendMap = blendMap;
        this.model = generateTerrain(loader);
    }

    private RawModel generateTerrain(Loader loader) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = j / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = i / ((float) VERTEX_COUNT - 1) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
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

}
