/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

/**
 * A window in the gui.
 * 
 * @author Josch Bosch
 */
public class Window extends Container {

    private static final String WINDOW_FOLDER = "window/";

    private String[] border = new String[9];

    private int edgePixel = 16;

    public Window(Container c, Vector4f positionAndSize) {
        super(c, positionAndSize);
        loadTextures("standard");
        createWindowContainer(positionAndSize);
    }

    private void loadTextures(String subfolder) {
        border[0] = WINDOW_FOLDER + subfolder + "/edge_top_left.png";
        border[1] = WINDOW_FOLDER + subfolder + "/line_horizontal_top.png";
        border[2] = WINDOW_FOLDER + subfolder + "/edge_top_right.png";
        border[3] = WINDOW_FOLDER + subfolder + "/line_vertical_left.png";
        border[4] = WINDOW_FOLDER + subfolder + "/line_vertical_right.png";
        border[5] = WINDOW_FOLDER + subfolder + "/edge_bottom_left.png";
        border[6] = WINDOW_FOLDER + subfolder + "/line_horizontal_bottom.png";
        border[7] = WINDOW_FOLDER + subfolder + "/edge_bottom_right.png";
        border[8] = WINDOW_FOLDER + subfolder + "/background.png";

    }

    private void createWindowContainer(Vector4f positionAndSize) {
        float edgeHeight = (float) edgePixel / Display.getHeight();
        float edgeWidth = (float) edgePixel / Display.getWidth();

        Container topLeft = new Container(this, new Vector4f(positionAndSize.x, positionAndSize.y, edgeWidth, edgeHeight));
        topLeft.setBackground(border[0]);
        Container topCenter =
            new Container(this, new Vector4f(positionAndSize.x + edgeWidth, positionAndSize.y, positionAndSize.z - 2 * edgeWidth,
                edgeHeight));
        topCenter.setBackground(border[1]);
        Container topRight =
            new Container(this, new Vector4f(positionAndSize.x + positionAndSize.z - edgeWidth, positionAndSize.y, edgeWidth,
                edgeHeight));
        topRight.setBackground(border[2]);
        Container centerLeft =
            new Container(this, new Vector4f(positionAndSize.x, positionAndSize.y + edgeHeight, edgeWidth,
                positionAndSize.w - 2 * edgeHeight));
        centerLeft.setBackground(border[3]);
        Container centerRight =
            new Container(this, new Vector4f(positionAndSize.x + positionAndSize.z - edgeWidth,
                positionAndSize.y + edgeHeight, edgeWidth, positionAndSize.w - 2 * edgeHeight));
        centerRight.setBackground(border[4]);
        Container bottonLeft =
            new Container(this, new Vector4f(positionAndSize.x, positionAndSize.y + positionAndSize.w - edgeHeight,
                edgeWidth, edgeHeight));
        bottonLeft.setBackground(border[5]);
        Container bottomCenter =
            new Container(this, new Vector4f(positionAndSize.x + edgeWidth,
                positionAndSize.y + positionAndSize.w - edgeHeight, positionAndSize.z - 2 * edgeWidth,
                edgeHeight));
        bottomCenter.setBackground(border[6]);
        Container bottomRight =
            new Container(this, new Vector4f(positionAndSize.x + positionAndSize.z - edgeWidth,
                positionAndSize.y + positionAndSize.w - edgeHeight, edgeWidth,
                edgeHeight));
        bottomRight.setBackground(border[7]);

        Container center =
            new Container(this, new Vector4f(positionAndSize.x + edgeWidth,
                positionAndSize.y + edgeHeight, positionAndSize.z - 2 * edgeWidth, positionAndSize.w - 2 * edgeHeight));
        center.setBackground(border[8]);
    }
}
