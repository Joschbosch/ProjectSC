/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map;

import java.util.List;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.physics.collision.OctreeNode;
import de.projectsc.modes.client.gui.RenderingSystem;
import de.projectsc.modes.client.gui.data.GUIScene;

/**
 * Extending class for drawing some more information than the standard system.
 * 
 * @author Josch Bosch
 */
public class MapEditorRenderingSystem extends RenderingSystem {

    private static final String MOUSE_ENTITY_ID = "mouseEntity";

    public MapEditorRenderingSystem(EntityManager entityManager, EventManager eventManager) {
        super(entityManager, eventManager);
    }

    /***
     * Extends standard method.
     * @param octree for super call
     * @param mouseEntity to draw
     * @param components to draw
     * @return the scene
     */
    public GUIScene createScene(OctreeNode octree, EditorMouseEntity mouseEntity, List<Component> components) {
        GUIScene scene = super.createScene(octree);
        scene.getPositions().put(MOUSE_ENTITY_ID, mouseEntity.getT().getPosition());
        scene.getRotations().put(MOUSE_ENTITY_ID, mouseEntity.getT().getRotation());
        scene.getScales().put(MOUSE_ENTITY_ID, mouseEntity.getT().getScale());
        return scene;
    }

}
