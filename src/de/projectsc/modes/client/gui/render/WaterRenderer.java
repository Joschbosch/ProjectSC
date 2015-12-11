/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.objects.Camera;
import de.projectsc.modes.client.gui.objects.terrain.water.WaterFrameBuffers;
import de.projectsc.modes.client.gui.objects.terrain.water.WaterTile;
import de.projectsc.modes.client.gui.shaders.WaterShader;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Renders water tiles.
 *
 * @author Josch Bosch
 */
public class WaterRenderer {

    private static final float TIME_SCALE = 1000.0f;

    private static final String DUDV_MAP = "terrain/waterDUDV.png";

    private static final float WAVE_SPEED = 0.03f;

    private RawModel quad;

    private final WaterShader shader;

    private final WaterFrameBuffers fbos;

    private final int dudvTexture;

    private float moveFactor = 0;

    public WaterRenderer(Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
        this.shader = new WaterShader();
        this.fbos = fbos;
        dudvTexture = Loader.loadTexture(DUDV_MAP);
        shader.start();
        shader.loadTextures();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        setUpVAO();
    }

    /**
     * 
     * Render water tiles.
     * 
     * @param water tiles to render
     * @param camera current camera
     * @param delta elapsed time since last frame
     */
    public void render(List<WaterTile> water, Camera camera, float delta) {
        prepareRender(camera, delta);
        for (WaterTile tile : water) {
            Matrix4f modelMatrix = Maths.createTransformationMatrix(
                new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
                new Vector3f(WaterTile.TILE_SIZE, WaterTile.TILE_SIZE, WaterTile.TILE_SIZE));
            shader.loadModelMatrix(modelMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
        }
        unbind();
    }

    private void prepareRender(Camera camera, float delta) {
        shader.start();
        shader.loadViewMatrix(camera);
        moveFactor += WAVE_SPEED * delta / TIME_SCALE;
        moveFactor %= 1;
        float waveyMoveFactor = (float) Math.sin(moveFactor * 2 * Math.PI) / 2f + (1.0f / 2.0f);
        shader.loadMoveFactor(waveyMoveFactor);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
    }

    private void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void setUpVAO() {
        // Just x and z vectex positions here, y is set to 0 in v.shader
        float[] vertices = { 0 - 1, 0 - 1, 0 - 1, 1, 1, 0 - 1, 1, 0 - 1, 0 - 1, 1, 1, 1 };
        quad = Loader.loadToVAO(vertices, 2);
    }

    /**
     * Dispose method.
     *
     */
    public void dispose() {
        shader.dispose();
    }

}
