/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.core.modes.client.common.data.UIElement;
import de.projectsc.core.modes.client.common.ui.elements.Menu;
import de.projectsc.core.modes.client.gui.data.UI;
import de.projectsc.core.modes.client.gui.data.View;
import de.projectsc.core.modes.client.gui.objects.text.Font;
import de.projectsc.core.modes.client.gui.objects.text.FontStore;
import de.projectsc.core.modes.client.gui.objects.text.FontType;
import de.projectsc.core.modes.client.gui.objects.text.GUIText;
import de.projectsc.core.modes.client.gui.objects.text.TextMaster;
import de.projectsc.core.modes.client.gui.textures.UITexture;
import de.projectsc.core.modes.client.gui.utils.Loader;

public class MenuView extends View {

    private Menu menu;

    private List<GUIText> lastText = new LinkedList<>();

    private FontType font;

    private float fontSize = 2f;

    private UITexture bg;

    public MenuView(UIElement element) {
        super(element);
        this.menu = (Menu) element;
        this.font = FontStore.getFont(Font.CANDARA);
        bg = new UITexture(Loader.loadTexture(menu.getBackground()), new Vector2f(0f, 0f), new Vector2f(0.5f, 0.5f));
    }

    @Override
    public void render(UI ui) {
        List<String> items = menu.getMenuItems();
        ui.addElement(bg, 0);
        for (GUIText lines : lastText) {
            TextMaster.removeText(lines);
        }
        int index = 0;
        for (String item : items) {
            GUIText menuItemText = new GUIText(item, fontSize, font, getPosition(index), 1.0f, true);
            menuItemText.setColour(1, 0, 0);
            if (index++ == menu.getChosenItem()) {
                menuItemText.setBorderWidth(0.5f);
                menuItemText.setOutlineColor(0, 0, 0);
            }
            TextMaster.loadText(menuItemText);
            lastText.add(menuItemText);
        }
        TextMaster.createAndLoadText("(0,0)", fontSize, font, new Vector2f(0.0f, 0.0f), 1.0f, false);
    }

    private Vector2f getPosition(int i) {
        return new Vector2f(0, 0.3f + 0.1f * i);
    }

}
