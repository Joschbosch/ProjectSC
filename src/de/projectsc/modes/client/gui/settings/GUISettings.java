/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.settings;

import org.lwjgl.opengl.GL11;
/**
 * Class for enabling/disabling opengl settings.
 * @author Josch Bosch
 */
public final class GUISettings {

    private GUISettings() {

    }

    /**
     * Enable culling for not render too much back faces.
     */
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    /**
     * Disable culling for rendering transparent faces.
     */
    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }
}
