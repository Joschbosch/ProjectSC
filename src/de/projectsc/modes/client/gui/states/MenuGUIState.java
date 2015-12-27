/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.messages.MessageConstants;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.states.CommonClientState;
import de.projectsc.modes.client.core.ui.UIElement;
import de.projectsc.modes.client.core.ui.UIManager;
import de.projectsc.modes.client.game.states.MenuState;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.views.ConsoleView;
import de.projectsc.modes.client.gui.ui.views.MenuView;

public class MenuGUIState implements GUIState, InputCommandListener {

    private Container container;

    private MenuView menu;

    private ConsoleView console;

    private UIElement menuState;

    @Override
    public boolean renderScene() {
        return false;
    }

    @Override
    public void initialize() {
        container = new Container();
        container.setBackground("images/bg.png");
        menu = new MenuView(container);
        console = new ConsoleView(container);
        this.menuState = UIManager.getElement(MenuState.class);
        InputConsumeManager.getInstance().addListener(this);
        console.setActive(false);
    }

    @Override
    public void update() {
        menu.update();
        console.update();
    }

    @Override
    public void getUIElements(UI ui) {
        container.render(ui);
    }

    @Override
    public void tearDown() {
        TextMaster.removeAll();
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (!console.isActive() && command.isKeyDown()) {
            if (command.getKey() == Keyboard.KEY_RETURN) {
                if (command.isKeyDown() && !command.isKeyRepeated()) {
                    if (MenuState.getInstance().getState() == MenuState.STATE_LOGIN) {
                        ((CommonClientState) menuState).sendMessage(new ClientMessage(MessageConstants.CONNECT));
                    }
                    if (MenuState.getInstance().getState() == MenuState.STATE_MAIN_MENU) {
                        ((CommonClientState) menuState).sendMessage(new ClientMessage(MessageConstants.CREATE_NEW_GAME_REQUEST));

                    }
                    if (MenuState.getInstance().getState() == MenuState.STATE_CREATE_GAME_MENU) {
                        ((CommonClientState) menuState).sendMessage(new ClientMessage(GameMessageConstants.START_GAME_REQUEST));
                    }
                }
                command.consume();
            }
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {

    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public boolean getCameraMoveable() {
        return false;
    }

    @Override
    public boolean isDebugModeActive() {
        return false;
    }
}
