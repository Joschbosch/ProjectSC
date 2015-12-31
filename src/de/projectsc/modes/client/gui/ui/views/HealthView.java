/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.views;

import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.gui.ui.View;
import de.projectsc.modes.client.gui.ui.basic.Container;

/**
 * View for players health.
 * 
 * @author Josch Bosch
 */
public class HealthView extends View {

    public HealthView(Container c) {
        super(c);
        Container cont = new Container(c, new Vector4f(0.0f, 0.75f, 0.42f, 0.25f));
        cont.setBackground("images/health.png");
    }

    @Override
    public void update() {}
}
