/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.gui.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.projectsc.client.gui.render.FontRenderer;
import de.projectsc.client.gui.tools.Loader;

/**
 * Master class for all texts to see.
 * 
 * @author Josch Bosch
 */
public final class TextMaster {

    private static Map<FontType, List<GUIText>> texts = new HashMap<>();

    private static FontRenderer renderer;

    private TextMaster() {}

    /**
     * Init text master.
     */
    public static void init() {
        renderer = new FontRenderer();
    }

    /**
     * Load up a text.
     * 
     * @param text to load
     */
    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);

    }

    /**
     * Render the texts.
     */
    public static void render() {
        renderer.render(texts);
    }

    /**
     * Remove a text from the screen.
     * 
     * @param text to remove
     */
    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            texts.remove(textBatch);
        }
    }

    /**
     * Clean up everything.
     */
    public static void cleanUp() {
        renderer.cleanUp();
    }
}
