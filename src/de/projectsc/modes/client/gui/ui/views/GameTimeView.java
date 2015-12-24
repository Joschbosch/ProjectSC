/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.views;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.data.View;
import de.projectsc.modes.client.gui.objects.text.Font;
import de.projectsc.modes.client.gui.objects.text.FontStore;
import de.projectsc.modes.client.gui.objects.text.GUIText;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.ui.BasicUIElement;
import de.projectsc.modes.client.ui.elements.GameTime;

public class GameTimeView extends View {

    private GUIText time = null;

    public GameTimeView(BasicUIElement element) {
        super(element);
    }

    @Override
    public void render(UI ui) {
        if (time != null) {
            TextMaster.removeText(time);
        }
        time =
            TextMaster.createAndLoadText("" + ((GameTime) element).getCurrentTimeString(), 3, FontStore.getFont(Font.CANDARA),
                new Vector2f(0, 0), 1, true);
    }
}
