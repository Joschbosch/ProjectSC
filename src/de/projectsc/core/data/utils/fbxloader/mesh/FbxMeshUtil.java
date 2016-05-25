/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.fbxloader.mesh;

import de.projectsc.core.data.utils.fbxloader.FBXElement;

public class FbxMeshUtil {

    public static double[] getDoubleArray(FBXElement el) {
        if (el.getPropertiesTypes()[0] == 'd') {
            // FBX 7.x
            return (double[]) el.getProperties().get(0);
        } else if (el.getPropertiesTypes()[0] == 'D') {
            // FBX 6.x
            double[] doubles = new double[el.getPropertiesTypes().length];
            for (int i = 0; i < doubles.length; i++) {
                doubles[i] = (Double) el.getProperties().get(i);
            }
            return doubles;
        } else {
            return null;
        }
    }

    public static int[] getIntArray(FBXElement el) {
        if (el.getPropertiesTypes()[0] == 'i') {
            // FBX 7.x
            return (int[]) el.getProperties().get(0);
        } else if (el.getPropertiesTypes()[0] == 'I') {
            // FBX 6.x
            int[] ints = new int[el.getPropertiesTypes().length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = (Integer) el.getProperties().get(i);
            }
            return ints;
        } else {
            return null;
        }
    }
}
