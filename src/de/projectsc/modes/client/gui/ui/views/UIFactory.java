/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.ui.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.modes.client.core.states.UIElementConstants;
import de.projectsc.modes.client.gui.data.View;
import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * Creates UI elements based on the basic element given.
 * 
 * @author Sascha Zur
 */
public final class UIFactory {

    private static final Log LOGGER = LogFactory.getLog(UIFactory.class);

    private UIFactory() {

    }

    /**
     * Creates UI elements based on the basic element given.
     * 
     * @param element to create view from.
     * @return new view
     */
    public static View createView(BasicUIElement element) {
        switch (element.getUiViewId()) {
        case UIElementConstants.CONSOLE:
            return new ConsoleView(element);
        case UIElementConstants.LOGIN:
            return new ConsoleView(element);
        case UIElementConstants.MENU:
            return new MenuView(element);
        case UIElementConstants.GAMETIMER:
            return new GameTimeView(element);
        default:
            LOGGER.error("Could not create view for id " + element.getUiViewId());
            return null;
        }
    }
}
