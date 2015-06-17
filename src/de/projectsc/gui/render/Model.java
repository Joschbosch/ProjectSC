/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.render;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Model {

    private static final Log LOGGER = LogFactory.getLog(Model.class);

    private List<Vector3f> vertices = new ArrayList<>();

    private List<Vector3f> normals = new ArrayList<>();

    private List<Vector2f> textures = new ArrayList<>();

    private List<Face> faces = new ArrayList<>();

    private int vboiId;

    private int vaoId;

    private int vboId;

    public Model(String filename) {
        try {
            List<String> fileContent = FileUtils.readLines(new File(Model.class.getResource("/meshes/" + filename).toURI()));

            for (String s : fileContent) {
                String[] split = s.split("\\s");
                if (split[0].equals("v")) {
                    vertices.add(new Vector3f(Float.valueOf(split[1]), Float.valueOf(split[2]), Float.valueOf(split[3])));
                } else if (s.startsWith("vt")) {
                    textures.add(new Vector2f(Float.valueOf(split[1]), Float.valueOf(split[2])));
                } else if (s.startsWith("vn")) {
                    normals.add(new Vector3f(Float.valueOf(split[1]), Float.valueOf(split[2]), Float.valueOf(split[3])));
                } else if (s.startsWith("f")) {
                    Vector3f vertexIndex = new Vector3f(Float.valueOf(split[1].split("/")[0]), Float.valueOf(split[2].split("/")[0]), Float
                        .valueOf(split[3].split("/")[0]));
                    Vector3f textureIndex = null;
                    if (split[1].split("/").length > 1) {
                        textureIndex = new Vector3f(Float.valueOf(split[1].split("/")[1]), Float.valueOf(split[2].split("/")[1]), Float
                            .valueOf(split[3].split("/")[1]));
                    }
                    Vector3f normalIndex = null;
                    if (split[1].split("/").length > 2) {
                        normalIndex = new Vector3f(Float.valueOf(split[1].split("/")[2]), Float.valueOf(split[2].split("/")[2]), Float
                            .valueOf(split[3].split("/")[2]));
                    }
                    faces.add(new Face(vertexIndex, textureIndex, normalIndex));
                }
            }
            FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer((vertices.size() * 3));
            for (Vector3f f : vertices) {
                f.store(verticesBuffer);
            }
            verticesBuffer.flip();

            int indicesCount = faces.size() * 3;
            IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indicesCount);
            for (Face f : faces) {
                indicesBuffer.put((int) f.vertex.x);
                indicesBuffer.put((int) f.vertex.y);
                indicesBuffer.put((int) f.vertex.z);
            }
            indicesBuffer.flip();

            // Create a new Vertex Array Object in memory and select it (bind)
            // A VAO can have up to 16 attributes (VBO's) assigned to it by default
            vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoId);
            vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
            // Put the VBO in the attributes list at index 0
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
            // Deselect (bind to 0) the VBO
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            // Deselect (bind to 0) the VAO
            GL30.glBindVertexArray(0);

            // Create a new VBO for the indices and select it (bind)
            vboiId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
            // Deselect (bind to 0) the VBO
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Error loading mesh for " + filename, e);
        }

    }

    public void render() {
        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, faces.size() * 3, GL11.GL_UNSIGNED_INT, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private class Face {

        private Vector3f vertex; // only index

        private Vector3f normal; // only index

        private Vector3f texture;

        public Face(Vector3f vertex, Vector3f textureIndex, Vector3f normalIndex) {
            super();
            this.vertex = vertex;
            this.texture = textureIndex;
            this.normal = normalIndex;
        }

        public Vector3f getVertex() {
            return vertex;
        }

        public Vector3f getNormal() {
            return normal;
        }
    }
}
