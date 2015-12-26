/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import de.projectsc.modes.client.gui.data.UI;

public interface GUIState {

    boolean renderScene();

    void initialize();

    void update();

    void render(UI ui);

    void tearDown();

}
