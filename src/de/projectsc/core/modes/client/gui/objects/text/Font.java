/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.objects.text;

/**
 * All fonts in the engine.
 * 
 * @author Josch Bosch
 */
public enum Font {
    /**
     * Font type.
     */
    CANDARA("Candara", "candara.png", "candara.fnt", 0.5f, 0.1f);

    private String fontName;

    private String textureName;

    private String fontFileName;

    private float width;

    private float edge;

    Font(String fontName, String textureName, String fontFileName, float width, float edge) {
        this.setFontName(fontName);
        this.setTextureName(textureName);
        this.setFontFileName(fontFileName);
        this.setWidth(width);
        this.setEdge(edge);
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontFileName() {
        return fontFileName;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getEdge() {
        return edge;
    }

    public void setEdge(float edge) {
        this.edge = edge;
    }
}
