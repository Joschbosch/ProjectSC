/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.ui.basic.Container;
import de.projectsc.modes.client.gui.ui.views.GameTimeView;

public class GameGUIState implements GUIState {

    private Container container;

    private GameTimeView gameTimeView;

    @Override
    public boolean renderScene() {
        return true;
    }

    @Override
    public void initialize() {
        container = new Container();
        gameTimeView = new GameTimeView(container);
    }

    @Override
    public void update() {
        gameTimeView.update();
    }

    @Override
    public void render(UI ui) {
        container.render(ui);

    }

    @Override
    public void tearDown() {
        // TODO Auto-generated method stub

    }

}
