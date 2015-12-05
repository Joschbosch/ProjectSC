/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.core.modes.client.common.UIElement;
import de.projectsc.core.modes.client.common.ui.elements.Menu;
import de.projectsc.core.modes.client.gui.TextMaster;
import de.projectsc.core.modes.client.gui.UI;
import de.projectsc.core.modes.client.gui.View;
import de.projectsc.core.modes.client.gui.text.FontType;
import de.projectsc.core.modes.client.gui.text.GUIText;
import de.projectsc.core.utils.Font;
import de.projectsc.core.utils.FontStore;

public class MenuView extends View {

    private Menu menuBox;

    private List<GUIText> lastText = new LinkedList<>();

    private FontType font;

    private float fontSize = 2f;

    public MenuView(UIElement element) {
        super(element);
        this.menuBox = (Menu) element;
        this.font = FontStore.getFont(Font.CANDARA);
    }

    @Override
    public void render(UI ui) {
        List<String> items = menuBox.getMenuItems();
        for (GUIText lines : lastText) {
            TextMaster.removeText(lines);
        }
        int index = 0;
        for (String item : items) {
            GUIText menuItemText = new GUIText(item, fontSize, font, getPosition(index++), 1.0f, true);
            menuItemText.setColour(1, 1, 1);
            TextMaster.loadText(menuItemText);
        }
    }

    private Vector2f getPosition(int i) {
        return new Vector2f(0, 0.3f + 0.1f * i);
    }

}
