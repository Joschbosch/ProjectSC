/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.utils;

import org.lwjgl.util.vector.Vector3f;

/**
 * Constants for the GUI part.
 * 
 * @author Josch Bosch
 */
public final class GUIConstants {

    /**
     * Constant.
     */
    public static final String SKYBOX_IMAGE_ROOT = "";

    /**
     * Constant.
     */
    public static final String TEXTURE_ROOT = "/textures/";

    /**
     * Constant.
     */
    public static final String BASIC_TEXTURE_ROOT = "basic/";

    /**
     * Constant.
     */
    public static final String BASIC_TEXTURE_WHITE = BASIC_TEXTURE_ROOT + "white.png";

    /**
     * Constant.
     */
    public static final String BASIC_TEXTURE_BLACK = BASIC_TEXTURE_ROOT + "black.png";

    /**
     * Constant.
     */
    public static final String BASIC_MESH_ROOT = "/meshes/";

    /**
     * Constant.
     */
    public static final String BASIC_MESH_PRIMITIVES_ROOT = BASIC_MESH_ROOT + "primitives/";

    /**
     * Constant.
     */
    public static final String BASIC_MESH_PRIMITIVES_SPHERE = BASIC_MESH_PRIMITIVES_ROOT + "sphere.obj";

    /**
     * Standard gravity.
     */
    public static final Vector3f GRAVITY_PARTICLES = new Vector3f(0.0f, -50f, 0.0f);

    /**
     * Standard line height.
     */
    public static final double TEXT_LINE_HEIGHT = 0.03f;

    /**
     * FOV.
     */
    public static final float FOV = 70f;

    /**
     * Frustrum near plane.
     */
    public static final float NEAR_PLANE = 0.1f;

    /**
     * Frustum far plane.
     */
    public static final float FAR_PLANE = 1000f;

    private GUIConstants() {

    }
}
