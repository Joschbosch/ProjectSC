/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui;

import java.util.Map;
import java.util.Set;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.events.movement.NewPositionEvent;
import de.projectsc.core.events.objects.CreateNewLightEvent;
import de.projectsc.core.events.objects.RemoveLightEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponent;
import de.projectsc.modes.client.gui.components.MeshRendererComponent;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.events.ChangeMeshRendererParameterEvent;
import de.projectsc.modes.client.gui.events.NewTextureEvent;

/**
 * System for rendering everything.
 * 
 * @author Josch Bosch
 */
public class RenderingSystem extends DefaultSystem {

    private static final String NAME = "Rendering System";

    public RenderingSystem() {
        super(NAME);
        EventManager.registerForEvent(NewPositionEvent.class, this);
        EventManager.registerForEvent(ChangeMeshRendererParameterEvent.class, this);
        EventManager.registerForEvent(NewTextureEvent.class, this);
        EventManager.registerForEvent(CreateNewLightEvent.class, this);
        EventManager.registerForEvent(RemoveLightEvent.class, this);
    }

    @Override
    public void processEvent(Event e) {
        if (EntityManager.hasComponent(e.getEntityId(), MeshRendererComponent.class)) {
            MeshRendererComponent c =
                ((MeshRendererComponent) EntityManager.getComponent(e.getEntityId(), MeshRendererComponent.NAME));
            if (e instanceof ChangeMeshRendererParameterEvent) {
                ChangeMeshRendererParameterEvent ev = (ChangeMeshRendererParameterEvent) e;
                c.setFakeLighting(ev.isFakeLightning());
                c.setIsTransparent(ev.isTransparent());
                c.setNumberOfRows(ev.getNumColums());
                c.setReflectivity(ev.getReflectivity());
                c.setShineDamper(ev.getShineDamper());
            } else if (e instanceof NewTextureEvent) {
                NewTextureEvent event = (NewTextureEvent) e;
                if (event.getTextureFile() != null) {
                    c.loadAndApplyTexture(event.getTextureFile());
                }
            }
        }
        if (e instanceof CreateNewLightEvent && hasComponent(e.getEntityId(), EmittingLightComponent.class)) {
            CreateNewLightEvent createNewLightEvent = (CreateNewLightEvent) e;
            if (createNewLightEvent.getPosition() != null) {
                getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                    createNewLightEvent.getPosition(), createNewLightEvent.getLight());
            } else {

                Transform pos = EntityManager.getEntity(e.getEntityId()).getTransform();
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
        Set<Long> entities = EntityManager.getAllEntites();
        for (Long entity : entities) {
            for (Component comp : EntityManager.getAllComponents(entity).values()) {
                if (comp instanceof GraphicalComponent) {
                    ((GraphicalComponent) comp).update(entity);
                }
            }
            if (hasComponent(entity, EmittingLightComponent.class)) {
                EmittingLightComponent c = getComponent(entity, EmittingLightComponent.class);
                Transform pos = EntityManager.getEntity(entity).getTransform();
                if (c != null && pos != null) {
                    c.updateLightPositionToEntity(entity, pos.getPosition());
                }
            }
            if (hasComponent(entity, MeshRendererComponent.class)) {
                getComponent(entity, MeshRendererComponent.class).update(entity);
            }

        }
    }

    private boolean hasComponent(Long entity, Class<? extends DefaultComponent> clazz) {
        return EntityManager.hasComponent(entity, clazz);
    }

    /**
     * Creates the scene that will be rendered afterwards.
     * 
     * @return scene to render
     */

    public GUIScene createScene() {
        Set<Long> entities = EntityManager.getAllEntites();
        GUIScene scene = new GUIScene();
        for (Long entity : entities) {
            Map<String, Component> allComponents = EntityManager.getAllComponents(entity);
            for (Component c : allComponents.values()) {
                if (c instanceof GraphicalComponent) {
                    GraphicalComponent gc = (GraphicalComponent) c;
                    gc.render(entity, scene);
                }
                c.addSceneInformation(entity, scene);
            }
            Transform pc = EntityManager.getEntity(entity).getTransform();
            scene.getPositions().put(entity, pc.getPosition());
            scene.getRotations().put(entity, pc.getRotation());
            scene.getScales().put(entity, pc.getScale());
        }
        return scene;
    }
}
