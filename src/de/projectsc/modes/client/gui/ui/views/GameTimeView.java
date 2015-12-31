/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.views;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.core.ui.UIManager;
import de.projectsc.modes.client.game.ui.controls.GameTime;
import de.projectsc.modes.client.gui.ui.View;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.basic.Label;

/**
 * View for the game time.
 * 
 * @author Josch Bosch
 */
public class GameTimeView extends View {

    private GameTime gameTime;

    private Label label;

    public GameTimeView(Container c) {
        super(c);
        gameTime = (GameTime) UIManager.getElement(GameTime.class);
        label = new Label(c, new Vector2f(0, 0));
        label.setFontSize(3);
        label.setCentered(true);
    }

    @Override
    public void update() {
        label.setText("" + gameTime.getCurrentTimeString());
    }
}
