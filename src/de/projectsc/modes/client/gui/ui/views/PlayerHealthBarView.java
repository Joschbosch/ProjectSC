/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.ui.views;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.core.ui.UIManager;
import de.projectsc.modes.client.game.ui.controls.PlayerHealthBar;
import de.projectsc.modes.client.gui.ui.View;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.basic.Label;

public class PlayerHealthBarView extends View {

    private PlayerHealthBar playerHealthBar;

    private Label label;

    public PlayerHealthBarView(Container c) {
        super(c);
        playerHealthBar = (PlayerHealthBar) UIManager.getElement(PlayerHealthBar.class);
        label = new Label(c, new Vector2f(0, 0.95f));
        label.setFontSize(1);
        label.setTextColor(0, 0, 1);
        label.setCentered(false);
    }

    @Override
    public void update() {
        label.setText("Health. " + playerHealthBar.getCurrentHealthStatus() + " Status: " + playerHealthBar.getCurrentStatus());
    }

}
