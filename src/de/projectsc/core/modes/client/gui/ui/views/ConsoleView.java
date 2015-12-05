/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.core.modes.client.common.UIElement;
import de.projectsc.core.modes.client.common.ui.elements.Console;
import de.projectsc.core.modes.client.gui.TextMaster;
import de.projectsc.core.modes.client.gui.UI;
import de.projectsc.core.modes.client.gui.View;
import de.projectsc.core.modes.client.gui.text.FontType;
import de.projectsc.core.modes.client.gui.text.GUIText;
import de.projectsc.core.modes.client.gui.tools.Loader;
import de.projectsc.core.modes.client.gui.ui.UITexture;
import de.projectsc.core.utils.Font;
import de.projectsc.core.utils.FontStore;

public class ConsoleView extends View {

    private static final Vector2f SIZE = new Vector2f(1.5f, 0.5f);

    private static final Vector2f POSITION = new Vector2f(-0.5f, 0.5f);

    private static final int MAXIMAL_LINES = 20;

    private Console console;

    private float fontSize = 0.7f;

    private FontType font;

    private Vector2f position;

    private UITexture bg;

    private List<GUIText> lastText = new LinkedList<>();

    private float linePadding = 0.018f;

    public ConsoleView(UIElement element) {
        super(element);
        console = (Console) element;
        this.font = FontStore.getFont(Font.CANDARA);
        this.position = new Vector2f(0, 0);
        bg = new UITexture(Loader.loadTexture("black.png"), POSITION, SIZE);
    }

    @Override
    public void render(UI ui) {
        for (GUIText lines : lastText) {
            TextMaster.removeText(lines);
        }
        if (console.isVisible()) {
            List<String> lines = console.getLines();
            int startValue = lines.size();
            if (startValue > MAXIMAL_LINES) {
                startValue -= MAXIMAL_LINES;
            } else {
                startValue = 0;
            }
            for (int i = startValue; i < lines.size(); i++) {
                Vector2f linePosition = new Vector2f(position);
                linePosition.y = position.y + SIZE.y - (i - startValue + 2) * linePadding;
                GUIText newLine = TextMaster.createAndLoadText(lines.get(i), fontSize, font, linePosition, 1.0f, false);
                lastText.add(newLine);
                newLine.setColour(1, 1, 1);
            }
            Vector2f linePosition = new Vector2f(position);
            linePosition.y = position.y + SIZE.y - linePadding - 0.001f;
            GUIText commandPromt =
                TextMaster.createAndLoadText("> " + console.getCurrentInput() + "_", fontSize, font, linePosition, 1.0f, false);
            commandPromt.setColour(1, 1, 1);
            lastText.add(commandPromt);
            ui.addElement(bg);
        }

    }
}
