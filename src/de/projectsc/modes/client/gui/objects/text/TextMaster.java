/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.objects.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Master class for all texts to see.
 * 
 * @author Josch Bosch
 */
public final class TextMaster {

    private static Map<FontType, List<GUIText>> texts = new HashMap<>();

    private TextMaster() {}

    /**
     * Init text master.
     */
    public static void init() {}

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
        text.setSize(getSize(data.getVertexPositions(), text));
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);

    }

    private static Vector2f getSize(float[] vertexPositions, GUIText text) {
        Vector2f size = new Vector2f(-Float.MAX_VALUE, -Float.MAX_VALUE);
        for (int i = 0; i < vertexPositions.length; i++) {
            if (i % 2 == 0) {
                float translated = vertexPositions[i] - text.getPosition().x;
                if (translated > size.x) {
                    size.x = vertexPositions[i];
                }
            } else {
                float translated = vertexPositions[i] - text.getPosition().y;
                if (translated > size.y) {
                    size.y = vertexPositions[i];
                }
            }
        }
        size.x /= 2;
        size.y /= 2;
        return size;
    }

    public static boolean hasText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        return textBatch.contains(text);
    }

    /**
     * Creates and loads a new text to the GUI.
     * 
     * @param text to display
     * @param fontSize size
     * @param font to use
     * @param position to use
     * @param maxLineLength until wrapping
     * @param centered true or false
     * @return the created text
     */
    public static GUIText createAndLoadText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength,
        boolean centered) {
        GUIText newText = new GUIText(text, fontSize, font, position, maxLineLength, centered);
        loadText(newText);
        return newText;
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
    public static void cleanUp() {}

    /**
     * Render the texts.
     * 
     * @return texts to render
     */
    public static Map<FontType, List<GUIText>> render() {
        return texts;
    }

    public static void removeAll() {
        texts.clear();
    }

}
