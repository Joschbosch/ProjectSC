/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.fbxloader.mesh;

import de.projectsc.core.data.utils.fbxloader.FBXElement;

public class FBXMesh {

    public static FBXMesh parseMesh(FBXElement modelRoot) {
        FBXElement verticesElement = modelRoot.getChildById("Vertices");
        double[] vertices = FbxMeshUtil.getDoubleArray(verticesElement);

        return null;

    }
}
