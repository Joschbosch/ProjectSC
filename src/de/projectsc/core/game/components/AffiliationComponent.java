/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.component.DefaultComponent;

public class AffiliationComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Affiliation Component";

    public String ownAffiliation = "red";

    @Override
    public boolean isValidForSaving() {
        return true;
    }

}
