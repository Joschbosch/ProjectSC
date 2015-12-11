/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.textures;

import java.nio.ByteBuffer;

/**
 * Data from a loaded texture.
 * 
 * @author Josch Bosch
 */
public class TextureData {

    private final int width;

    private final int height;

    private final ByteBuffer buffer;

    public TextureData(int width, int height, ByteBuffer buffer) {
        super();
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

}
