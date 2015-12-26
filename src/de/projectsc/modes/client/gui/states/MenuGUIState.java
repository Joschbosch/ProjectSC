/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.gui.ui.ViewManager;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.views.ConsoleView;
import de.projectsc.modes.client.gui.ui.views.MenuView;

public class MenuGUIState implements GUIState {

    private Container container;

    private MenuView menu;

    private ConsoleView console;

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
    }

    @Override
    public void update() {
        menu.update();
        console.update();
    }

    @Override
    public void render(UI ui) {
        container.render(ui);
    }

    @Override
    public void tearDown() {
        ViewManager.unregisteElement(menu);
        TextMaster.removeAll();
    }
}
