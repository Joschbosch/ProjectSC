package de.projectsc.modes.client.gui.postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.postProcessing.gaussianBlur.HorizontalBlur;
import de.projectsc.modes.client.gui.postProcessing.gaussianBlur.VerticalBlur;
import de.projectsc.modes.client.gui.utils.Loader;

public class PostProcessing {

    private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };

    private static RawModel quad;

    private static ConstrastChanger constrastChanger;

    private static HorizontalBlur hBlur;

    private static VerticalBlur vBlur;

    private static Identical identical;

    public static void init() {
        quad = Loader.loadToVAO(POSITIONS, 2);
        identical = new Identical();
        constrastChanger = new ConstrastChanger();
        hBlur = new HorizontalBlur(Display.getWidth(), Display.getHeight());
        vBlur = new VerticalBlur(Display.getWidth(), Display.getHeight());

    }

    public static void doPostProcessing(int colorTexture) {
        start();
        int currentTexture = colorTexture;
        hBlur.render(currentTexture);
        currentTexture = hBlur.getOutputTexture();
        vBlur.render(currentTexture);
        currentTexture = vBlur.getOutputTexture();
        constrastChanger.render(currentTexture);

        identical.render(currentTexture);
        end();
    }

    public static void dispose() {
        constrastChanger.dispose();
        hBlur.dispose();
        vBlur.dispose();
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
