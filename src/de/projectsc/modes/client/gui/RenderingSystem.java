/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.events.entity.movement.NotifyTransformUpdateEvent;
import de.projectsc.core.events.entity.objects.CreateLightEvent;
import de.projectsc.core.events.entity.objects.RemoveLightEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponent;
import de.projectsc.modes.client.gui.components.MeshRendererComponent;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.events.UpdateMeshRendererParameterEvent;
import de.projectsc.modes.client.gui.events.UpdateTextureEvent;

/**
 * System for rendering everything.
 * 
 * @author Josch Bosch
 */
public class RenderingSystem extends DefaultSystem {

    private static final String NAME = "Rendering System";

    public RenderingSystem(EntityManager entityManager, EventManager eventManager) {
        super(NAME, entityManager, eventManager);
        eventManager.registerForEvent(NotifyTransformUpdateEvent.class, this);
        eventManager.registerForEvent(UpdateMeshRendererParameterEvent.class, this);
        eventManager.registerForEvent(UpdateTextureEvent.class, this);
        eventManager.registerForEvent(CreateLightEvent.class, this);
        eventManager.registerForEvent(RemoveLightEvent.class, this);
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof EntityEvent) {
            processEvent((EntityEvent) e);
        }
    }

    /**
     * Process an entity event.
     * 
     * @param e event to process
     */
    public void processEvent(EntityEvent e) {
        if (entityManager.hasComponent(e.getEntityId(), MeshRendererComponent.class)) {
            MeshRendererComponent c =
                ((MeshRendererComponent) entityManager.getComponent(e.getEntityId(), MeshRendererComponent.NAME));
            if (e instanceof UpdateMeshRendererParameterEvent) {
                UpdateMeshRendererParameterEvent ev = (UpdateMeshRendererParameterEvent) e;
                c.setFakeLighting(ev.isFakeLightning());
                c.setIsTransparent(ev.isTransparent());
                c.setNumberOfRows(ev.getNumColums());
                c.setReflectivity(ev.getReflectivity());
                c.setShineDamper(ev.getShineDamper());
            } else if (e instanceof UpdateTextureEvent) {
                UpdateTextureEvent event = (UpdateTextureEvent) e;
                if (event.getTextureFile() != null) {
                    try {
                        c.loadAndApplyTexture(event.getTextureFile().getCanonicalPath());
                    } catch (IOException e1) {
                        // LOGGER.error(e1.getStackTrace());
                    }
                }
            }
        }
        if (e instanceof CreateLightEvent && hasComponent(e.getEntityId(), EmittingLightComponent.class)) {
            CreateLightEvent createNewLightEvent = (CreateLightEvent) e;
            if (createNewLightEvent.getPosition() != null) {
                getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                    createNewLightEvent.getPosition(), createNewLightEvent.getLight());
            } else {

                Transform pos = entityManager.getEntity(e.getEntityId()).getTransform();
                getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                    pos.getPosition(), createNewLightEvent.getLight());
            }
        }
        if (e instanceof RemoveLightEvent && hasComponent(e.getEntityId(), EmittingLightComponent.class)) {
            getComponent(e.getEntityId(), EmittingLightComponent.class).removeLight(((RemoveLightEvent) e).getLight());
        }
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getAllEntites();
        for (String entity : entities) {
            for (Component comp : entityManager.getAllComponents(entity).values()) {
                if (comp instanceof GraphicalComponent) {
                    ((GraphicalComponent) comp).update(tick);
                }
            }
            if (hasComponent(entity, MeshRendererComponent.class)) {
                getComponent(entity, MeshRendererComponent.class).update(tick);
            }

        }

    }

    /**
     * Creates the scene that will be rendered afterwards.
     * 
     * @return scene to render
     */

    public GUIScene createScene() {
        Set<String> entities = entityManager.getAllEntites();
        GUIScene scene = new GUIScene();
        for (String entity : entities) {
            Map<String, Component> allComponents = entityManager.getAllComponents(entity);
            for (Component c : allComponents.values()) {
                if (c instanceof GraphicalComponent) {
                    GraphicalComponent gc = (GraphicalComponent) c;
                    gc.render(entity, scene);
                }
                c.addSceneInformation(scene);
            }
            Transform pc = entityManager.getEntity(entity).getTransform();
            scene.getPositions().put(entity, pc.getPosition());
            scene.getRotations().put(entity, pc.getRotation());
            scene.getScales().put(entity, pc.getScale());
        }

        return scene;
    }
}
