/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.data.View;
import de.projectsc.modes.client.gui.objects.text.Font;
import de.projectsc.modes.client.gui.objects.text.FontStore;
import de.projectsc.modes.client.gui.objects.text.FontType;
import de.projectsc.modes.client.gui.objects.text.GUIText;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.gui.textures.UITexture;
import de.projectsc.modes.client.gui.utils.Loader;
import de.projectsc.modes.client.ui.BasicUIElement;
import de.projectsc.modes.client.ui.elements.Console;

/**
 * View for the {@link Console}.
 * 
 * @author Josch Bosch
 */
public class ConsoleView extends View {

    private static final Vector2f SIZE = new Vector2f(2f, 0.5f);

    private static final Vector2f POSITION = new Vector2f(0f, 0f);

    private static final int MAXIMAL_LINES = 20;

    private final Console console;

    private final float fontSize = 0.7f;

    private final FontType font;

    private final Vector2f position;

    private final UITexture bg;

    private final List<GUIText> lastText = new LinkedList<>();

    private final float linePadding = 0.018f;

    public ConsoleView(BasicUIElement element) {
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
                newLine.setRenderOrder(1);
            }
            Vector2f linePosition = new Vector2f(position);
            linePosition.y = position.y + SIZE.y - linePadding - 0.001f;
            GUIText commandPrompt =
                TextMaster.createAndLoadText("> " + console.getCurrentInput() + "_", fontSize, font, linePosition, 1.0f, false);
            commandPrompt.setColour(1, 1, 1);
            lastText.add(commandPrompt);
            commandPrompt.setRenderOrder(1);
            ui.addElement(bg, UI.AFTER_TEXT);
        }

    }
}
