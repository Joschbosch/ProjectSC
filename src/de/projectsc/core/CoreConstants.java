/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core;

/**
 * Core constants class.
 *
 * @author Josch Bosch
 */
public final class CoreConstants {

    /**
     * Constant.
     */
    public static final String SCHEME_DIRECTORY_NAME = "schemes";

    /**
     * Constant.
     */
    public static final String SCHEME_DIRECTORY_PREFIX = "S";

    /**
     * Constant.
     */
    public static final String MODEL_FILENAME = "model.obj";

    /**
     * Constant.
     */
    public static final String TEXTURE_FILENAME = "texture.png";

    /**
     * Constant.
     */
    public static final String ENTITY_FILENAME = "entity.ent";

    /**
     * Constant.
     */
    public static final float[] SQUARE_VERTICES = {
        -0.5f, -0.5f, 0.0f,
        -0.5f, 0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.5f, 0.5f, 0.0f,
    };

    /**
     * Name of bounding box file.
     */
    public static final String BOX_FILENAME = "boundingBox.obj";

    private CoreConstants() {

    }
}
