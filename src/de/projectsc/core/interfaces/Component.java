/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.projectsc.core.data.Scene;

public interface Component {

    List<String> getRequiredComponents();

    List<String> getRequiredBy();

    void addRequiredByComponent(String componentName);

    void removeRequiredByComponent(String componentName);

    void setActive(boolean b);

    void addDebugMode(Scene scene);

    boolean isValidForSaving();

    Map<String, Object> serialize(File schemaFolder);

    void deserialize(Map<String, Object> serialized, File schemaFile);

    String getComponentName();
}
