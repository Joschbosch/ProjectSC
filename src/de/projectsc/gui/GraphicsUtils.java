/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

/**
 * Utility methods for OpenGL.
 * 
 * @author Josch Bosch
 */
public final class GraphicsUtils {

    private static final int NUMBER_24 = 24;

    private static final int NUMBER_8 = 8;

    private static final int NUMBER_16 = 16;

    private static final int HEX_0X_FF = 0xFF;

    private static final int BYTES_PER_PIXEL = 4;

    private GraphicsUtils() {

    }

    /**
     * Draws the text in given font and color to (x,y).
     * 
     * @param text to draw
     * @param x coordinate
     * @param y coordinate
     * @param font to draw
     * @param color to draw
     */
    public static void drawText(String text, int x, int y, Font font, Color color) {
        BufferedImage textImage = GraphicsUtils.createTextImage(text, font, color);

        int textureID = GraphicsUtils.loadTexture(textImage);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);

        glTexCoord2f(1, 0);
        glVertex2f(x + textImage.getWidth(), y);

        glTexCoord2f(1, 1);
        glVertex2f(x + textImage.getWidth(), y + textImage.getHeight());

        glTexCoord2f(0, 1);
        glVertex2f(x, y + textImage.getHeight());
        glEnd();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    /**
     * Draws the given text to given location (x,y), using standard fonts, colors, etc.
     * 
     * @param text to draw
     * @param x coordinate
     * @param y coordinate
     */
    public static void drawText(String text, int x, int y) {
        final int twelve = 12;
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, twelve);
        drawText(text, x, y, font, Color.red);
    }

    private static BufferedImage createTextImage(String text, Font font, Color color) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int textwidth = fm.stringWidth(text);
        int textheight = fm.getHeight();
        BufferedImage test = new BufferedImage(textwidth, textheight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = test.createGraphics();

        g2d.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2d.fillRect(0, 0, textwidth, textheight);

        g2d.setColor(color);
        g2d.setFont(font);
        g2d.drawString(text, 0, textheight / 2);
        return test;
    }

    /**
     * Loads a texture for the given BufferedImage.
     * 
     * @param image to make the texture
     * @return id of the texture.
     */
    public static int loadTexture(BufferedImage image) {

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); // 4

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> NUMBER_16) & HEX_0X_FF));
                buffer.put((byte) ((pixel >> NUMBER_8) & HEX_0X_FF));
                buffer.put((byte) (pixel & HEX_0X_FF));
                buffer.put((byte) ((pixel >> NUMBER_24) & HEX_0X_FF));
            }
        }

        buffer.flip();
        int textureID = glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        // All RGB bytes are aligned to each other and each component is 1 byte
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        // Upload the texture data and generate mip maps (for scaling)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, image.getWidth(), image.getHeight(), 0,
            GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        // Setup what to do when the texture has to be scaled
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
            GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
            GL11.GL_LINEAR_MIPMAP_LINEAR);

        return textureID;
    }

}
