/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.gui.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.shaders.SkyboxShader;
import de.projectsc.client.gui.tools.Loader;

/**
 * Renderer just for the skybox.
 * 
 * @author Josch Bosch
 */
public class SkyboxRenderer {

    private static final int DAY_END = 21000;

    private static final int DUSK_END = 8000;

    private static final int MIDNIGHT = 0;

    private static final int NIGHT_END = 5000;

    private static final int DAY_TIME = 24000;

    private static final float SIZE = 500f;

    private static final String[] TEXTURE_FILES = { "sky/day/right", "sky/day/left", "sky/day/top", "sky/day/bottom", "sky/day/back",
        "sky/day/front" };

    private static final String[] NIGHT_TEXTURE_FILES = { "sky/night/right", "sky/night/left", "sky/night/top", "sky/night/bottom",
        "sky/night/back",
        "sky/night/front" };

    private static final float[] VERTICES = {
        -SIZE, SIZE, -SIZE,
        -SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,

        -SIZE, -SIZE, SIZE,
        -SIZE, -SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, SIZE, SIZE,
        -SIZE, -SIZE, SIZE,

        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,

        -SIZE, -SIZE, SIZE,
        -SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, -SIZE, SIZE,
        -SIZE, -SIZE, SIZE,

        -SIZE, SIZE, -SIZE,
        SIZE, SIZE, -SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        -SIZE, SIZE, SIZE,
        -SIZE, SIZE, -SIZE,

        -SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, SIZE
    };

    private final RawModel cube;

    private final int texture;

    private final int nightTexture;

    private final SkyboxShader shader;

    private float time = 0;

    public SkyboxRenderer(Matrix4f projectionMatrix) {
        cube = Loader.loadToVAO(VERTICES, 3);
        texture = Loader.loadCubeMap(TEXTURE_FILES);
        nightTexture = Loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Render method.
     * 
     * @param elapsedTime since last frame.
     * @param camera c
     * @param fogR value
     * @param fogG value
     * @param fogB value
     */
    public void render(long elapsedTime, Camera camera, float fogR, float fogG, float fogB) {
        shader.start();
        shader.loadViewMatrix(camera, elapsedTime);
        shader.loadFogColor(fogR, fogG, fogB);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures(elapsedTime);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures(long elapsedTime) {
        time += elapsedTime;
        time %= DAY_TIME;
        int texture1;
        int texture2;
        float blendFactor;
        if (time >= MIDNIGHT && time < NIGHT_END) {
            texture1 = nightTexture;
            texture2 = nightTexture;
            blendFactor = (time - MIDNIGHT) / (NIGHT_END - MIDNIGHT);
        } else if (time >= NIGHT_END && time < DUSK_END) {
            texture1 = nightTexture;
            texture2 = texture;
            blendFactor = (time - NIGHT_END) / (DUSK_END - NIGHT_END);
        } else if (time >= DUSK_END && time < DAY_END) {
            texture1 = texture;
            texture2 = texture;
            blendFactor = (time - DUSK_END) / (DAY_END - DUSK_END);
        } else {
            texture1 = texture;
            texture2 = nightTexture;
            blendFactor = (time - DAY_END) / (DAY_TIME - DAY_END);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(blendFactor);
    }
}
