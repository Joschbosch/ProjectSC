/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.ui.views;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.core.ui.UIManager;
import de.projectsc.modes.client.game.ui.controls.Menu;
import de.projectsc.modes.client.gui.data.View;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.basic.Label;

/**
 * The graphical representation of the {@link Menu}.
 * 
 * @author Josch Bosch
 */
public class MenuView extends View {

    private final Menu menu;

    private final float fontSize = 2f;

    private Label[] labels;

    private int currentChosenLabel = -1;

    public MenuView(Container c) {
        super(c);
        this.menu = (Menu) UIManager.getElement(Menu.class);
        labels = new Label[menu.getMenuItems().size()];
        int index = 0;
        for (String item : menu.getMenuItems()) {
            Label label = new Label(c, getPosition(index));
            label.setFontSize(fontSize);
            label.setText(item);
            label.setTextColor(0, 1, 0);
            label.setOutlineColor(1, 1, 1);
            label.setCentered(true);
            label.setBorderWidth(0.5f);
            labels[index++] = label;
        }
    }

    private Vector2f getPosition(int i) {
        return new Vector2f(0, 0.3f + 0.1f * i);
    }

    @Override
    public void update() {
        if (menu.getChosenItem() != currentChosenLabel) {
            if (currentChosenLabel != -1) {
                labels[currentChosenLabel].setOutlineColor(1, 1, 1);
            }
            labels[menu.getChosenItem()].setOutlineColor(1, 0, 0);
            currentChosenLabel = menu.getChosenItem();
        }
    }
}
