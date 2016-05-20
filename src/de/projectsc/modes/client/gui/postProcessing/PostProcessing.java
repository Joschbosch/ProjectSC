package de.projectsc.modes.client.gui.postProcessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Main class for the post processing pipeline.
 * 
 * @author Josch Bosch
 */
public final class PostProcessing {

    private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };

    private static RawModel quad;

    private static List<PostProcessingEffect> effects;

    private static Identical identical;

    private PostProcessing() {

    }

    /**
     * Initialize post processing.
     */
    public static void init() {
        quad = Loader.loadToVAO(POSITIONS, 2);

        identical = new Identical();

        effects = new ArrayList<PostProcessingEffect>(5);
        // effects.add(new ConstrastChanger(Display.getWidth(), Display.getHeight()));
        // effects.add(new VerticalBlur(Display.getWidth(), Display.getHeight()));
        // effects.add(new HorizontalBlur(Display.getWidth(), Display.getHeight()));

    }

    /**
     * Start post processing pipeline with registered effects.
     * 
     * @param colorTexture to render to.
     */
    public static void doPostProcessing(int colorTexture) {
        start();
        int currentTexture = colorTexture;
        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).render(currentTexture);
            currentTexture = effects.get(i).getOutputTexture();
        }
        identical.render(currentTexture);
        end();
    }

    /**
     * Delete everything.
     */
    public static void dispose() {
        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).dispose();
        }
        identical.dispose();
    }

    private static void start() {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

}
