/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game.ai;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;

public class AIControlledComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "AI Controlled Component";

    public AIControlledComponent() {
        setType(ComponentType.PHYSICS);
        setComponentName(NAME);
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

}
