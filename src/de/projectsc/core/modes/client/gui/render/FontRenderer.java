package de.projectsc.core.modes.client.gui.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.projectsc.core.modes.client.gui.objects.text.FontType;
import de.projectsc.core.modes.client.gui.objects.text.GUIText;
import de.projectsc.core.modes.client.gui.shaders.FontShader;

/**
 * Renderer for all text things in the game.
 * 
 * @author Josch Bosch
 */
public class FontRenderer {

    private FontShader shader;

    public FontRenderer() {
        shader = new FontShader();
    }

    /**
     * Render all texts given.
     * 
     * @param texts to render
     */
    public void render(Map<FontType, List<GUIText>> texts) {
        prepare();
        for (FontType font : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
            for (GUIText text : texts.get(font)) {
                renderText(text);
            }
        }
        endRendering();
    }

    private void renderText(GUIText text) {
        GL30.glBindVertexArray(text.getMesh());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        shader.loadColor(text.getColor());
        shader.loadTranslation(text.getPosition());
        shader.loadFontAttributes(text.getFont());
        shader.loadTextAttributes(text);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
    }

    private void endRendering() {
        shader.stop();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

    }

    /**
     * Dispose.
     */
    public void dispose() {
        shader.dispose();
    }

}
