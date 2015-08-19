/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.shaders.BillboardShader;
import de.projectsc.client.gui.tools.Loader;

/**
 * Renderer for billboards.
 * 
 * @author Josch Bosch
 */
public class BillboardRenderer {

    private final BillboardShader shader;

    private final RawModel quad;

    private final Matrix4f projectionMatrix;

    private Camera camera;

    public BillboardRenderer(Matrix4f projectionMatrix) {
        // Just x and z vertex positions here, y is set to 0 in v.shader
        float[] vertices = {
            -1, -1,
            1, -1,
            -1, 1,
            1, -1,
            -1, 1,
            1, 1 };
        quad = Loader.loadToVAO(vertices, 2);
        shader = new BillboardShader();
        this.projectionMatrix = projectionMatrix;
    }

    /**
     * Render all billboards.
     * 
     * @param billboards to render
     */
    public void render(List<Billboard> billboards) {

        shader.start();
        // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (Billboard board : billboards) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, board.getTexture());
            shader.loadPositionAttributes(camera.createViewMatrix(), projectionMatrix);
            shader.loadBillboardAttributes(board.getPosition(), board.getSize());
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.stop();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
