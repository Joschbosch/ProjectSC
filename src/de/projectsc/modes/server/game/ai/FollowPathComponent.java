/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game.ai;

import java.util.HashMap;
import java.util.Map;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

/**
 * Component that lets an entity follow a given path.
 * 
 * @author Josch Bosch
 */
public class FollowPathComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Follow Path Component";

    private int pathIDToFollow = -1;

    private int nextPathPoint = 0;

    public FollowPathComponent() {
        setType(ComponentType.GAME);
        setComponentName(NAME);
    }

    @Override
    public Component cloneComponent() {
        FollowPathComponent fpc = new FollowPathComponent();
        fpc.setPathIDToFollow(pathIDToFollow);
        return fpc;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> result = new HashMap<>();
        result.put("pathIDToFollow", pathIDToFollow);
        return result;

    }

    @Override
    public void loadConfiguration(Map<String, Object> loadedConfiguration) {
        setPathIDToFollow((int) loadedConfiguration.get("pathIDToFollow"));
    }

    public void setPathIDToFollow(int pathIDToFollow) {
        this.pathIDToFollow = pathIDToFollow;
    }

    public int getPathIDToFollow() {
        return pathIDToFollow;
    }

    public int getNextPathPoint() {
        return nextPathPoint;
    }

    public void setNextPathPoint(int nextPathPoint) {
        this.nextPathPoint = nextPathPoint;
    }

}
