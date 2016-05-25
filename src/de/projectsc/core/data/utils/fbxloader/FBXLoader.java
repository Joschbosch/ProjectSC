/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.fbxloader;

import java.io.IOException;

import de.projectsc.core.data.utils.fbxloader.mesh.FBXMesh;

public class FBXLoader {

    public static void main(String[] args) {
        loadFBX("dragon.fbx");
    }

    public static void loadFBX(String filename) {
        try {
            FBXFile fbx = FBXReader.readFBX(FBXLoader.class.getResourceAsStream("/models/animated/" + filename));
            for (FBXElement elt : fbx.getRootElements().values()) {
                // print(elt, 0);
            }
            readObjectsPart(fbx.getRootElements().get("Objects"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readObjectsPart(FBXElement fbxElement) {
        FBXMesh mesh = FBXMesh.parseMesh(fbxElement.getChildById("Geometry"));
    }

    private static void print(FBXElement model, int i) {
        for (int j = 0; j < i; j++) {
            System.out.print(" ");
        }
        System.out.println("ID : " + model.getId());
        for (int j = 0; j < i; j++) {
            System.out.print(" ");
        }
        System.out.println("FBXProperties : " + model.getFbxProperties());
        for (int j = 0; j < i; j++) {
            System.out.print(" ");
        }
        System.out.println("Properties : " + model.getProperties());
        for (int j = 0; j < i; j++) {
            System.out.print(" ");
        }
        System.out.println("No Children : " + model.getChildren().size());
        if (!model.getChildren().isEmpty()) {
            for (FBXElement child : model.getChildren()) {
                print(child, i + 1);
            }
        }
    }
}
