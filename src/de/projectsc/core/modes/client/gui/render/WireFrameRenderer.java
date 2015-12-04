/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.shaders.WireFrameShader;
import de.projectsc.core.modes.client.gui.tools.Maths;
import de.projectsc.core.utils.BoundingBox;

/**
 * This class will render collision boxes onto the screen.
 * 
 * @author Josch Bosch
 */
public class WireFrameRenderer {

    private final WireFrameShader shader;

    public WireFrameRenderer(WireFrameShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Renders all textured models without switching vaos to often.
     * 
     * @param boundingBoxes to render.
     */
    public void render(Map<RawModel, List<BoundingBox>> boundingBoxes) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        for (RawModel model : boundingBoxes.keySet()) {
            prepareModel(model);
            List<BoundingBox> batch = boundingBoxes.get(model);
            for (BoundingBox b : batch) {
                prepareInstance(b);
                GL11.glDrawElements(GL11.GL_TRIANGLES, b.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    private void prepareModel(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(BoundingBox b) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(b.getPosition(), b.getRotation().x, b.getRotation().y, b.getRotation().z, b.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
