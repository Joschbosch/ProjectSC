/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.data.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.ModelData;
import de.projectsc.core.data.physics.Vertex;

/**
 * Load an *.obj model file.
 * 
 * @author Josch Bosch
 */
public final class OBJFileLoader {

    private static final Log LOGGER = LogFactory.getLog(OBJFileLoader.class);

    private static final String FACE_SEPERATION_CHAR = "/";

    private static Map<String, ModelData> loadedModels = new TreeMap<>();

    private OBJFileLoader() {

    }

    /**
     * @param filePath to load obj file from
     * @return data
     */
    public static ModelData loadOBJFromSchema(String filePath) {
        if (new File(filePath).exists()) {
            return loadOBJFromFileSystem(filePath);
        } else {
            InputStream objFile = OBJFileLoader.class.getResourceAsStream(filePath);
            return loadOBJ(filePath, objFile);
        }
    }

    /**
     * @param filePath to load obj file from
     * @return data
     */
    public static ModelData loadOBJFromResources(String filePath) {
        InputStream objFile = OBJFileLoader.class.getResourceAsStream(filePath);
        return loadOBJ(filePath, objFile);
    }

    /**
     * @param filePath to load obj file from
     * @return data
     */
    public static ModelData loadOBJFromFileSystem(String filePath) {
        InputStream objFile;
        try {
            objFile = new FileInputStream(new File(filePath));
            return loadOBJ(filePath, objFile);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getStackTrace());
        }
        return null;
    }

    /**
     * Load given file.
     * 
     * @param filePath path to file
     * @param objInputStream file from obj
     * @return {@link ModelData} with all information
     */
    public static ModelData loadOBJ(String filePath, InputStream objInputStream) {
        if (loadedModels.containsKey(filePath)) {
            return loadedModels.get(filePath);
        }
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(objInputStream));
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
                Vertex v0 = processVertex(vertex1, vertices, indices);
                Vertex v1 = processVertex(vertex2, vertices, indices);
                Vertex v2 = processVertex(vertex3, vertices, indices);
                calculateTangents(v0, v1, v2, textures);
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
        float[] tangentsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
            texturesArray, normalsArray, tangentsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        ModelData data = new ModelData(verticesArray, texturesArray, tangentsArray, normalsArray, indicesArray,
            furthest);
        loadedModels.put(filePath, data);
        return data;
    }

    private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2,
        List<Vector2f> textures) {
        Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
        Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
        Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.scale(deltaUv2.y);
        delatPos2.scale(deltaUv1.y);
        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
        tangent.scale(r);
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static Vertex processVertex(String[] vertex, List<Vertex> vertices,
        List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
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
        float[] normalsArray, float[] tangentsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            Vector3f tangent = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
            tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;

        }
        return furthestPoint;
    }

    private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
        int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,
                    newNormalIndex, indices, vertices);
            } else {
                Vertex duplicateVertex = previousVertex.duplicate(vertices.size());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }
        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

}
