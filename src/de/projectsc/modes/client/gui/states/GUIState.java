/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import de.projectsc.modes.client.gui.data.UI;

public interface GUIState {

    boolean renderScene();

    void initialize();

    void update();

    void getUIElements(UI ui);

    void tearDown();

    boolean getCameraMoveable();

    boolean isDebugModeActive();

}
