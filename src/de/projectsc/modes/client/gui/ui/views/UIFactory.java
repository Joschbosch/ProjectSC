/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.modes.client.common.StateConstants;
import de.projectsc.modes.client.common.data.UIElement;
import de.projectsc.modes.client.gui.data.View;

public final class UIFactory {

    private static final Log LOGGER = LogFactory.getLog(UIFactory.class);

    private UIFactory() {

    }

    public static View createView(UIElement element) {
        switch (element.getUiViewId()) {
        case StateConstants.CONSOLE:
            return new ConsoleView(element);
        case StateConstants.LOGIN:
            return new ConsoleView(element);
        case StateConstants.MENU:
            return new MenuView(element);
        default:
            LOGGER.error("Could not create view for id " + element.getUiViewId());
            return null;
        }
    }
}
