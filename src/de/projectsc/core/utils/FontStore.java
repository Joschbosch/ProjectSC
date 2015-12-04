/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.modes.client.gui.text.FontType;
import de.projectsc.core.modes.client.gui.tools.Loader;

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
        try {
            File fontImageFile = new File(FontStore.class.getResource("/fonts/" + font.getTextureName()).toURI());
            File fontFile = new File(FontStore.class.getResource("/fonts/" + font.getFontFileName()).toURI());
            FontType fontType = new FontType(Loader.loadTexture(fontImageFile), fontFile);
            fontType.setEdge(font.getEdge());
            fontType.setWidth(font.getWidth());
            return fontType;
        } catch (URISyntaxException e) {
            LOGGER.error("Could not load font " + font.name() + ": " + e.getMessage());
        }
        return null;
    }
}
