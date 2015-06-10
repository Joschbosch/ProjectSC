/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * All fonts in the game.
 * 
 * @author Josch Bosch
 */
public enum GameFont {
    /**
     * Standard font.
     */
    GLOBAL("SmoothieShoppe.ttf");

    private static final Log LOGGER = LogFactory.getLog(GameFont.class);

    private static final Map<GameFont, Font> FONTS = new HashMap<>();

    private final String filename;

    GameFont(String fontFile) {
        filename = fontFile;
    }

    /**
     * Load all fonts.
     */
    public static void loadFonts() {
        for (GameFont font : GameFont.values()) {
            URL fontUrl;
            try {
                fontUrl = GameFont.class.getResource("/fonts/" + font.getFilename());
                Font currrentFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(currrentFont);
                FONTS.put(font, currrentFont);
            } catch (FontFormatException | IOException e) {
                LOGGER.error("Could not load fonts: ", e);
            }
        }

    }

    /**
     * Retrieve font.
     * 
     * @param fontname to retreive
     * @return the font
     */
    public static Font getFont(GameFont fontname) {
        return FONTS.get(fontname);
    }

    public String getFilename() {
        return filename;
    }
}
