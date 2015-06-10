/*
 * Copyright (C) 2015
 */

package de.projectsc.gui.states;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

/**
 * Utility methods for OpenGL.
 * 
 * @author Josch Bosch
 */
public final class GraphicsUtils {

    private static final int BYTES_PER_PIXEL = 4;

    private GraphicsUtils() {

    }

    /**
     * Draws the given text to given location (x,y), using standard fonts, colors, etc.
     * 
     * @param text to draw
     * @param x coordinate
     * @param y coordinate
     */
    public static void drawText(String text, int x, int y) {
        BufferedImage textImage = GraphicsUtils.createTextImage(text);

        int textureID = GraphicsUtils.loadTexture(textImage);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //
        // glPushMatrix();
        glEnable(GL_TEXTURE_2D); // Enable texturing
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
        glDisable(GL_TEXTURE_2D); // Enable texturing
    }

    private static BufferedImage createTextImage(String text) {
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int textwidth = fm.stringWidth(text);
        int textheight = fm.getHeight();
        // It's of course also possible to just load an image using ImageIO.load()
        BufferedImage test = new BufferedImage(textwidth, textheight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = test.createGraphics();

        g2d.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2d.fillRect(0, 0, textwidth, textheight); // A transparent white background

        g2d.setColor(Color.red);
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
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        return textureID;
    }

}
