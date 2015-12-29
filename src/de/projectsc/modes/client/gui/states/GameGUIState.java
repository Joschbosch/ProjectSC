/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.events.input.MouseButtonClickedEvent;
import de.projectsc.core.events.input.MousePositionChangedEvent;
import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.manager.ClientEventManager;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.views.GameTimeView;
import de.projectsc.modes.client.gui.ui.views.HealthView;
import de.projectsc.modes.client.gui.utils.MousePicker;

public class GameGUIState implements GUIState, InputCommandListener {

    private Container container;

    private GameTimeView gameTimeView;

    private MousePicker mousePicker;

    private boolean debugMode;

    @Override
    public void initialize() {
        container = new Container();
        gameTimeView = new GameTimeView(container);
        new HealthView(container);
        InputConsumeManager.getInstance().addListener(this);
    }

    @Override
    public void update() {
        gameTimeView.update();
    }

    @Override
    public void getUIElements(UI ui) {
        container.render(ui);

    }

    @Override
    public void tearDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean renderScene() {
        return true;
    }

    @Override
    public boolean getCameraMoveable() {
        return true;
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (command.isKeyDown() && !command.isKeyRepeated() && command.getKey() == Keyboard.KEY_P) {
            debugMode = !debugMode;
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {
        if (!isUIElementHit(command)) {
            if (command.getButton() == -1) {
                ClientEventManager.getInstance().fireEvent(
                    new MousePositionChangedEvent(mousePicker.getCurrentRay(), mousePicker.getCurrentCameraPosition()));
            }
            if (command.isButtonDown(1) || command.isRepeatedDown()) {
                ClientEventManager.getInstance().fireEvent(
                    new MouseButtonClickedEvent(1, true, mousePicker.getCurrentRay(),
                        mousePicker.getCurrentCameraPosition(), mousePicker.getCurrentTerrainPoint()));
            }
        }
    }

    @Override
    public boolean isDebugModeActive() {
        return debugMode;
    }

    private boolean isUIElementHit(MouseInputCommand command) {
        return false;
    }

    public void setMousePicker(MousePicker picker) {
        this.mousePicker = picker;
    }
}
