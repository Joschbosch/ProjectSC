/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.render;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.gui.models.RawModel;

/**
 * Loads an obj model file to a {@link RawModel}.
 *
 * @author Josch Bosch
 */
public final class ModelLoader {

    private static final String VERTEX_FACE_SEPARATOR = "/";

    private static final Log LOGGER = LogFactory.getLog(ModelLoader.class);

    private ModelLoader() {

    }

    /**
     * Load model.
     * 
     * @param filename of the model
     * @param loader for storing the arrays
     * @return the loaded model.
     */
    public static RawModel loadModel(String filename, Loader loader) {
        try {
            List<String> lines = FileUtils.readLines(new File(ModelLoader.class.getResource("/meshes/" + filename).toURI()));
            List<Vector3f> vertices = new ArrayList<>();
            List<Vector2f> textures = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();

            float[] verticesArray = null;
            float[] normalsArray = null;
            float[] textureArray = null;
            int[] indicesArray = null;

            for (String line : lines) {
                String[] split = line.split("\\s");
                switch (split[0]) {
                case "v":
                    Vector3f vertex = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
                    vertices.add(vertex);
                    break;

                case "vt":
                    Vector2f vertexTexture = new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2]));
                    textures.add(vertexTexture);
                    break;

                case "vn":
                    Vector3f vertexNormal =
                        new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
                    normals.add(vertexNormal);
                    break;

                case "f":
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    String[] vertex1 = split[1].split(VERTEX_FACE_SEPARATOR);
                    String[] vertex2 = split[2].split(VERTEX_FACE_SEPARATOR);
                    String[] vertex3 = split[3].split(VERTEX_FACE_SEPARATOR);
                    processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                    processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                    processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                    break;

                default:
                    break;

                }
            }
            verticesArray = new float[vertices.size() * 3];
            indicesArray = new int[indices.size()];

            int vertexPoint = 0;

            for (Vector3f vertex : vertices) {
                verticesArray[vertexPoint++] = vertex.x;
                verticesArray[vertexPoint++] = vertex.y;
                verticesArray[vertexPoint++] = vertex.z;
            }

            for (int i = 0; i < indices.size(); i++) {
                indicesArray[i] = indices.get(i);
            }

            return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load model " + filename + " :", e);
            return null;
        }
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals,
        float[] textureArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTexture = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTexture.x;
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentTexture.y;
        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);

        normalsArray[currentVertexPointer * 3] = currentNormal.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z;

    }
}
