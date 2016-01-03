/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.objects.text;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Stores all fonts available.
 * 
 * @author Josch Bosch
 */
public final class FontStore {

    private static final Log LOGGER = LogFactory.getLog(FontStore.class);

    private static Map<Font, FontType> fonts = new HashMap<>();

    private FontStore() {}

    /**
     * Get a font (or load it if it isn't yet).
     * 
     * @param font to load
     * @return the font type
     */
    public static FontType getFont(Font font) {
        if (fonts.get(font) == null) {
            fonts.put(font, loadFont(font));
        }
        return fonts.get(font);
    }

    private static FontType loadFont(Font font) {
        InputStream fontImageFile = FontStore.class.getResourceAsStream("/fonts/" + font.getTextureName());
        InputStream fontFile = FontStore.class.getResourceAsStream("/fonts/" + font.getFontFileName());
        FontType fontType = new FontType(Loader.loadTexture(fontImageFile, "png"), fontFile);
        fontType.setEdge(font.getEdge());
        fontType.setWidth(font.getWidth());
        LOGGER.info("Loaded font " + font.getFontName());
        return fontType;
    }
}
