/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.client.gui.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.CoreConstants;

/**
 * Load an *.obj model file.
 * 
 * @author Josch Bosch
 */
public final class NewOBJFileLoader {

    private static final Log LOGGER = LogFactory.getLog(NewOBJFileLoader.class);

    private static final String FACE_SEPERATION_CHAR = "/";

    private static Map<File, ModelData> loadedModels = new TreeMap<>();

    private NewOBJFileLoader() {

    }

    /**
     * @param filePath to load obj file from
     * @return data
     */
    public static ModelData loadOBJ(String filePath) {
        try {
            File objFile = new File(NewOBJFileLoader.class.getResource(filePath + "/" + CoreConstants.MODEL_FILENAME).toURI());
            return loadOBJ(objFile);
        } catch (URISyntaxException e) {
            LOGGER.error(e);
        }
        return null;
    }

    /**
     * Load given file.
     * 
     * @param objFile file from obj
     * @return {@link ModelData} with all information
     */
    public static ModelData loadOBJ(File objFile) {
        if (loadedModels.containsKey(objFile)) {
            return loadedModels.get(objFile);
        }
        BufferedReader reader = null;
        try {
            FileReader isr = null;
            isr = new FileReader(objFile);
            reader = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extention");
        }
        if (reader != null) {
            String line;
            List<Vertex> vertices = new ArrayList<Vertex>();
            List<Vector2f> textures = new ArrayList<Vector2f>();
            List<Vector3f> normals = new ArrayList<Vector3f>();
            List<Integer> indices = new ArrayList<Integer>();
            try {
                while (true) {
                    line = reader.readLine();
                    if (line.startsWith("v ")) {
                        String[] currentLine = line.split("\\s");
                        Vector3f vertex = new Vector3f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3]));
                        Vertex newVertex = new Vertex(vertices.size(), vertex);
                        vertices.add(newVertex);

                    } else if (line.startsWith("vt ")) {
                        String[] currentLine = line.split(" ");
                        Vector2f texture = new Vector2f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]));
                        textures.add(texture);
                    } else if (line.startsWith("vn ")) {
                        String[] currentLine = line.split(" ");
                        Vector3f normal = new Vector3f(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3]));
                        normals.add(normal);
                    } else if (line.startsWith("f ")) {
                        break;
                    }
                }
                while (line != null && line.startsWith("f ")) {
                    String[] currentLine = line.split(" ");
                    String[] vertex1 = currentLine[1].split(FACE_SEPERATION_CHAR);
                    String[] vertex2 = currentLine[2].split(FACE_SEPERATION_CHAR);
                    String[] vertex3 = currentLine[3].split(FACE_SEPERATION_CHAR);
                    processVertex(vertex1, vertices, indices);
                    processVertex(vertex2, vertices, indices);
                    processVertex(vertex3, vertices, indices);
                    line = reader.readLine();
                }
                reader.close();

            } catch (IOException e) {
                System.err.println("Error reading the file");
            }
            removeUnusedVertices(vertices);
            float[] verticesArray = new float[vertices.size() * 3];
            float[] texturesArray = new float[vertices.size() * 2];
            float[] normalsArray = new float[vertices.size() * 3];
            float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
            int[] indicesArray = convertIndicesListToArray(indices);
            ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
                furthest);
            loadedModels.put(objFile, data);
            return data;
        } else {
            LOGGER.error("Could not load model " + objFile.getAbsolutePath());
            return null;
        }
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = -1;
        if (!vertex[0].isEmpty()) {
            index = Integer.parseInt(vertex[0]) - 1;
        }
        Vertex currentVertex = vertices.get(index);
        int textureIndex = -1;
        if (!vertex[1].isEmpty()) {
            textureIndex = Integer.parseInt(vertex[1]) - 1;
        }
        int normalIndex = -1;
        if (!vertex[2].isEmpty()) {
            normalIndex = Integer.parseInt(vertex[2]) - 1;
        }
        if (!currentVertex.isSet()) {
            if (textureIndex != -1) {
                currentVertex.setTextureIndex(textureIndex);
            }
            if (normalIndex != -1) {
                currentVertex.setNormalIndex(normalIndex);
            }
            if (index != -1) {
                indices.add(index);
            }
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                vertices);
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
        List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
        float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }

    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
        int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                    indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }

        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

}
