/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.ui.views;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.data.NewCommandLineEvent;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.ui.UIManager;
import de.projectsc.modes.client.game.ui.controls.Console;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
import de.projectsc.modes.client.gui.ui.View;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.basic.Label;
import de.projectsc.modes.client.gui.ui.basic.TextInput;
import de.projectsc.modes.client.gui.ui.basic.TextInputConsumer;
import de.projectsc.modes.client.gui.utils.GUIConstants;

/**
 * View for the {@link Console}.
 * 
 * @author Josch Bosch
 */
public class ConsoleView extends View implements InputCommandListener, TextInputConsumer {

    private static final Vector4f POSITION_AND_SIZE = new Vector4f(0, 0.5f, 1f, 0.5f);

    private static final int MAXIMAL_LINES = 23;

    private Console console = null;

    private final float fontSize = 0.7f;

    private Label textLabel;

    private Container consoleContainer;

    private TextInput textInput;

    public ConsoleView(Container c) {
        super(c);
        console = (Console) UIManager.getElement(Console.class);
        consoleContainer = new Container(c, POSITION_AND_SIZE);
        consoleContainer.setBackground(GUIConstants.BASIC_TEXTURE_BLACK);
        textLabel = new Label(consoleContainer, new Vector2f(0, 0));
        textLabel.setFontSize(fontSize);
        textLabel.setTextColor(1, 1, 1);
        textInput = new TextInput(consoleContainer, new Vector2f(0, POSITION_AND_SIZE.y - 0.02f));
        textInput.setFontSize(fontSize);
        textInput.setTextColor(1, 1, 1);
        textInput.setPrefix("> ");
        textInput.setSuffix("_");
        textInput.addTextInputConsumer(this);
        consoleContainer.setVisible(false);
        consoleContainer.setOrder(UI.FOREGROUND);
        InputConsumeManager.getInstance().addListener(this);
    }

    @Override
    public void update() {
        if (console.isActive()) {
            List<String> lines = console.getLines();
            String text = "";
            for (int i = 0; i < MAXIMAL_LINES - lines.size(); i++) {
                text += " \n";
            }
            int start = 0;
            if (lines.size() > MAXIMAL_LINES) {
                start = lines.size() - MAXIMAL_LINES;
            }
            for (int i = start; i < lines.size(); i++) {
                text += lines.get(i) + " \n";
            }
            textLabel.setText(text);
            consoleContainer.setVisible(true);
            consoleContainer.setActive(true);
        } else {
            textLabel.setText("");
            consoleContainer.setVisible(false);
            consoleContainer.setActive(false);

        }
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.FIRST;
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (command.getKey() == Keyboard.KEY_F1 && command.isKeyDown() && !command.isKeyRepeated()) {
            console.setActive(!console.isActive());
            command.consume();
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {

    }

    @Override
    public void consumeText(String newLine) {
        fireEvent(new NewCommandLineEvent(newLine));
    }

}
