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
 * Component that defines this entity as a path point. 
 * @author Josch Bosch
 */
public class PathPointComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Path Point Component";

    private int pathID = -1;

    private int groupOrderNumber = -1;

    public PathPointComponent() {
        setType(ComponentType.GAME);
        setComponentName(NAME);
    }

    @Override
    public Component cloneComponent() {
        PathPointComponent ppc = new PathPointComponent();
        ppc.setPathID(pathID);
        ppc.setGroupOrderNumber(groupOrderNumber);
        return ppc;
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> result = new HashMap<>();
        result.put("pathID", pathID);
        result.put("groupOrderNumber", groupOrderNumber);
        return result;
    }

    @Override
    public void loadConfiguration(Map<String, Object> serialized) {
        setPathID((int) serialized.get("pathID"));
        setGroupOrderNumber((int) serialized.get("groupOrderNumber"));
    }

    public int getPathID() {
        return pathID;
    }

    public void setPathID(int pathID) {
        this.pathID = pathID;
    }

    public int getGroupOrderNumber() {
        return groupOrderNumber;
    }

    public void setGroupOrderNumber(int groupOrderNumber) {
        this.groupOrderNumber = groupOrderNumber;
    }

}
