/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.ui.views;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.ui.UIManager;
import de.projectsc.modes.client.game.ui.controls.Menu;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
import de.projectsc.modes.client.gui.ui.View;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.basic.Label;
import de.projectsc.modes.client.gui.ui.basic.Window;

/**
 * The graphical representation of the {@link Menu}.
 * 
 * @author Josch Bosch
 */
public class MenuView extends View implements InputCommandListener {

    private final Menu menu;

    private final float fontSize = 2f;

    private Label[] labels;

    private int currentChosenLabel = -1;

    private int chosenItem;

    private Window menuWindow;

    public MenuView(Container c) {
        super(c);
        this.menu = (Menu) UIManager.getElement(Menu.class);
        float size = 0.4f;
        menuWindow = new Window(c, new Vector4f(0.5f - size / 2, 0.5f - size / 2, size, size));
        labels = new Label[menu.getMenuItems().size()];
        int index = 0;
        for (String item : menu.getMenuItems()) {
            Label label = new Label(menuWindow, getPosition(index, size));
            label.setFontSize(fontSize);
            label.setText(item);
            label.setTextColor(0, 1, 0);
            label.setOutlineColor(1, 1, 1);
            label.setCentered(true);
            label.setBorderWidth(0.5f);
            labels[index++] = label;
        }
        chosenItem = 0;
        InputConsumeManager.getInstance().addListener(this);

    }

    private Vector2f getPosition(int i, float size) {
        return new Vector2f(0, 2 * size / 3f * i + size / 2);
    }

    @Override
    public void update() {
        if (chosenItem != currentChosenLabel) {
            if (currentChosenLabel != -1) {
                labels[currentChosenLabel].setOutlineColor(1, 1, 1);
            }
            labels[chosenItem].setOutlineColor(1, 0, 0);
            currentChosenLabel = chosenItem;
        }
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (command.isKeyDown()) {
            if (command.getKey() == Keyboard.KEY_DOWN) {
                chosenItem = (chosenItem + 1) % menu.getMenuItems().size();
            }
        }
        if (command.isKeyDown() && !command.isKeyRepeated()) {
            if (command.getKey() == Keyboard.KEY_UP) {
                if (chosenItem > 0) {
                    chosenItem--;
                } else {
                    chosenItem = menu.getMenuItems().size() - 1;
                }
            }
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {}

}
