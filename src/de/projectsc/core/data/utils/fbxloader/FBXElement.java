/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.fbxloader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FBXElement {

    private String id;

    private List<Object> properties;

    /*
     * Y - signed short C - boolean I - signed integer F - float D - double L - long R - byte array S - string f - array of floats i - array
     * of ints d - array of doubles l - array of longs b - array of boleans c - array of unsigned bytes (represented as array of ints)
     */
    private char[] propertiesTypes;

    private List<FBXElement> children = new ArrayList<FBXElement>();

    public FBXElement(int propsCount) {
        this.properties = new ArrayList<Object>(propsCount);
        this.propertiesTypes = new char[propsCount];
    }

    public FBXElement getChildById(String name) {
        for (FBXElement element : children) {
            if (element.id.equals(name)) {
                return element;
            }
        }
        return null;
    }

    public List<FBXElement> getChildrenById(String name) {
        List<FBXElement> result = new LinkedList<>();
        for (FBXElement element : children) {
            if (element.id.equals(name)) {
                result.add(element);
            }
        }
        return result;
    }

    public List<FBXElement> getFbxProperties() {
        List<FBXElement> props = new ArrayList<FBXElement>();
        FBXElement propsElement = null;
        boolean legacy = false;

        for (FBXElement element : children) {
            if (element.id.equals("Properties70")) {
                propsElement = element;
                break;
            } else if (element.id.equals("Properties60")) {
                legacy = true;
                propsElement = element;
                break;
            }
        }

        if (propsElement == null) {
            return props;
        }

        for (FBXElement prop : propsElement.children) {
            if (prop.id.equals("P") || prop.id.equals("Property")) {
                if (legacy) {
                    char[] types = new char[prop.propertiesTypes.length + 1];
                    types[0] = prop.propertiesTypes[0];
                    types[1] = prop.propertiesTypes[0];
                    System.arraycopy(prop.propertiesTypes, 1, types, 2, types.length - 2);

                    List<Object> values = new ArrayList<Object>(prop.properties);
                    values.add(1, values.get(0));

                    FBXElement dummyProp = new FBXElement(types.length);
                    dummyProp.children = prop.children;
                    dummyProp.id = prop.id;
                    dummyProp.propertiesTypes = types;
                    dummyProp.properties = values;
                    props.add(dummyProp);
                } else {
                    props.add(prop);
                }
            }
        }

        return props;
    }

    @Override
    public String toString() {
        return "FBXElement[id=" + id + ", numProps=" + properties.size() + ", numChildren=" + children.size() + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Object> getProperties() {
        return properties;
    }

    public void setProperties(List<Object> properties) {
        this.properties = properties;
    }

    public List<FBXElement> getChildren() {
        return children;
    }

    public void setChildren(List<FBXElement> children) {
        this.children = children;
    }

    public char[] getPropertiesTypes() {
        return propertiesTypes;
    }
}
