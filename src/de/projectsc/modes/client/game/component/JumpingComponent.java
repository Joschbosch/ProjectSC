/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.game.component;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.physic.PhysicsComponent;

public class JumpingComponent extends PhysicsComponent {

    /**
     * ID.
     */
    public static final String NAME = "Jumping Component";

    private float jumpTime = 0;

    private boolean goingUp = true;

    private float previousOffset = 0;

    public JumpingComponent() {
        setComponentName(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update(long elapsed) {

    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public float getJumpTime() {
        return jumpTime;
    }

    public void setJumpTime(float jumpTime) {
        this.jumpTime = jumpTime;
    }

    public boolean isGoingUp() {
        return goingUp;
    }

    public void setGoingUp(boolean goingUp) {
        this.goingUp = goingUp;
    }

    public float getPreviousOffset() {
        return previousOffset;
    }

    public void setPreviousOffset(float previousOffset) {
        this.previousOffset = previousOffset;
    }

}
