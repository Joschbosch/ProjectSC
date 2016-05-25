/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.fbxloader;

import java.util.HashMap;
import java.util.Map;

public class FBXFile {

    private Map<String, FBXElement> rootElements = new HashMap<>();

    private long version;

    @Override
    public String toString() {
        return "FBXFile[version=" + version + ",numElements=" + rootElements.size() + "]";
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void addRootElement(FBXElement e) {
        rootElements.put(e.getId(), e);
    }

    public Map<String, FBXElement> getRootElements() {
        return rootElements;
    }
}
