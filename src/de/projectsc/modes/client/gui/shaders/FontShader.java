package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.modes.client.gui.objects.text.FontType;
import de.projectsc.modes.client.gui.objects.text.GUIText;

/**
 * Shader for text.
 * 
 * @author Josch Bosch
 */
public class FontShader extends Shader {

    private static final String VERTEX_FILE = "fontShader.vert";

    private static final String FRAGMENT_FILE = "fontShader.frag";

    private int locationColor;

    private int locationTranslation;

    private int locationWidth;

    private int locationEdge;

    private int locationBorderWidth;

    private int locationBorderEdge;

    private int locationOutlineColor;

    private int locationShadowOffset;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationColor = super.getUniformLocation("color");
        locationTranslation = super.getUniformLocation("translation");

        locationWidth = super.getUniformLocation("width");
        locationEdge = super.getUniformLocation("edge");
        locationBorderWidth = super.getUniformLocation("borderWidth");
        locationBorderEdge = super.getUniformLocation("borderEdge");
        locationOutlineColor = super.getUniformLocation("outlineColor");
        locationShadowOffset = super.getUniformLocation("offset");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    /**
     * @param color to load
     */
    public void loadColor(Vector3f color) {
        super.loadVector(locationColor, color);
    }

    /**
     * 
     * @param translation to load
     */
    public void loadTranslation(Vector2f translation) {
        super.loadVector(locationTranslation, translation);
    }

    /**
     * 
     * @param font to load from
     */
    public void loadFontAttributes(FontType font) {
        super.loadFloat(locationWidth, font.getWidth());
        super.loadFloat(locationEdge, font.getEdge());
    }

    /**
     * @param text to load from
     */
    public void loadTextAttributes(GUIText text) {
        super.loadFloat(locationBorderWidth, text.getBorderWidth());
        super.loadFloat(locationBorderEdge, text.getBorderEdge());
        super.loadVector(locationOutlineColor, text.getOutlineColor());
        super.loadVector(locationShadowOffset, text.getShadowOffset());
    }
}
