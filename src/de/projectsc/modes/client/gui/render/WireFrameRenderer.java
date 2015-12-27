/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.render;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.ModelData;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.core.data.utils.OBJFileLoader;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.shaders.WireFrameShader;
import de.projectsc.modes.client.gui.utils.GUIConstants;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * This class will render collision boxes onto the screen.
 * 
 * @author Josch Bosch
 */
// TODO Rework representation of bounding box model
public class WireFrameRenderer {

    private static final Log LOGGER = LogFactory.getLog(WireFrameRenderer.class);

    private final WireFrameShader shader;

    private RawModel sphere = null;

    private RawModel cube;

    private RawModel line;

    public WireFrameRenderer(WireFrameShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        try {
            ModelData data =
                OBJFileLoader.loadOBJ(new File(WireFrameRenderer.class.getResource(GUIConstants.BASIC_MESH_PRIMITIVES_SPHERE).toURI()));
            sphere = Loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            float[] verticesCube = {
                -0.5f, 0, -0.5f,
                0.5f, 0, -0.5f,

                0.5f, 0, -0.5f,
                0.5f, 0, 0.5f,

                0.5f, 0, 0.5f,
                -0.5f, 0, 0.5f,

                -0.5f, 0, 0.5f,
                -0.5f, 0, -0.5f,

                -0.5f, 1f, -0.5f,
                0.5f, 1f, -0.5f,

                0.5f, 1f, -0.5f,
                0.5f, 1f, 0.5f,

                0.5f, 1f, 0.5f,
                -0.5f, 1f, 0.5f,

                -0.5f, 1f, 0.5f,
                -0.5f, 1f, -0.5f,

                -0.5f, 0f, -0.5f,
                -0.5f, 1f, -0.5f,

                0.5f, 0f, -0.5f,
                0.5f, 1f, -0.5f,

                0.5f, 0f, 0.5f,
                0.5f, 1f, 0.5f,

                -0.5f, 0f, 0.5f,
                -0.5f, 1f, 0.5f
            };
            cube = Loader.loadToVAO(verticesCube, 3);

            float[] verticesLine = { -0.5f, 0, -0.5f,
                0.5f, 0, -0.5f };

            line = Loader.loadToVAO(verticesLine, 3);

        } catch (URISyntaxException e) {
            LOGGER.error("Could not load sphere model: ", e);
        }

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Renders all textured models without switching vaos to often.
     * 
     * @param wireFrames to render.
     */
    public void render(List<WireFrame> wireFrames) {

        prepareModel(sphere);
        for (WireFrame wireframe : wireFrames) {
            if (WireFrame.SPHERE.equals(wireframe.getModelType())) {
                // prepareInstance(wireframe.getPosition(), wireframe.getRotation(), wireframe.getScale(), wireframe.getColor());
                // GL11.glLineWidth(wireframe.getLineWidth());
                // GL11.glDrawElements(GL11.GL_LINE_STRIP, sphere.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
        }
        unbindTexturedModel();
        prepareModel(cube);
        for (WireFrame wireframe : wireFrames) {
            if (WireFrame.CUBE.equals(wireframe.getModelType())) {
                prepareInstance(wireframe.getPosition(), wireframe.getRotation(), wireframe.getScale(), wireframe.getColor());
                GL11.glLineWidth(wireframe.getLineWidth());
                GL11.glDrawArrays(GL11.GL_LINES, 0, cube.getVertexCount());
            }
        }
        unbindTexturedModel();
        prepareModel(line);
        for (WireFrame wireframe : wireFrames) {
            if (WireFrame.LINE.equals(wireframe.getModelType())) {
                prepareInstance(wireframe.getPosition(), wireframe.getRotation(), wireframe.getScale(), wireframe.getColor());
                GL11.glLineWidth(wireframe.getLineWidth());
                GL11.glDrawArrays(GL11.GL_LINES, 0, cube.getVertexCount());
            }
        }
        unbindTexturedModel();
        GL11.glLineWidth(1f);
    }

    private void prepareModel(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Vector3f positon, Vector3f rotation, Vector3f scale, Vector3f color) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(positon, rotation.x, rotation.y, rotation.z, scale);
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadColor(color);
    }
}
