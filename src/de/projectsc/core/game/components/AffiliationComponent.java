/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.game.Affiliation;
import de.projectsc.core.interfaces.Component;
/**
 * Component to set affiliation of a unit. 
 * @author Josch Bosch
 */
public class AffiliationComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Affiliation Component";

    private Affiliation ownAffiliation = Affiliation.NEUTRAL;

    private Affiliation enemyAffiliation = Affiliation.NEUTRAL;

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    @Override
    public Component cloneComponent() {
        return new AffiliationComponent();
    }

    public Affiliation getOwnAffiliation() {
        return ownAffiliation;
    }

    public void setOwnAffiliation(Affiliation ownAffiliation) {
        this.ownAffiliation = ownAffiliation;
    }

    public Affiliation getEnemyAffiliation() {
        return enemyAffiliation;
    }

    public void setEnemyAffiliation(Affiliation enemyAffiliation) {
        this.enemyAffiliation = enemyAffiliation;
    }

}
