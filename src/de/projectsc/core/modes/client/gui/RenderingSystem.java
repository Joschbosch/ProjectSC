/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui;

import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.EngineSystem;
import de.projectsc.core.EntityManager;
import de.projectsc.core.EventManager;
import de.projectsc.core.data.Event;
import de.projectsc.core.entities.Component;
import de.projectsc.core.entities.components.physic.PositionComponent;
import de.projectsc.core.events.ChangeModelParameterEvent;
import de.projectsc.core.events.CreateNewLightEvent;
import de.projectsc.core.events.MoveEvent;
import de.projectsc.core.events.NewModelOrTextureEvent;
import de.projectsc.core.events.NewPositionEvent;
import de.projectsc.core.events.RemoveLightEvent;
import de.projectsc.core.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.core.modes.client.gui.components.GraphicalComponent;
import de.projectsc.core.modes.client.gui.components.ModelAndTextureComponent;
import de.projectsc.core.modes.client.gui.data.Scene;

public class RenderingSystem extends EngineSystem {

    private static final String NAME = "Rendering System";

    public RenderingSystem() {
        super(NAME);
        EventManager.registerForEvent(NewPositionEvent.class, this);
        EventManager.registerForEvent(ChangeModelParameterEvent.class, this);
        EventManager.registerForEvent(MoveEvent.class, this);
        EventManager.registerForEvent(NewModelOrTextureEvent.class, this);
        EventManager.registerForEvent(CreateNewLightEvent.class, this);
        EventManager.registerForEvent(RemoveLightEvent.class, this);

    }

    @Override
    public void processEvent(Event e) {
        if (EntityManager.hasComponent(e.getEntityId(), ModelAndTextureComponent.class)) {
            ModelAndTextureComponent c =
                ((ModelAndTextureComponent) EntityManager.getComponent(e.getEntityId(), ModelAndTextureComponent.NAME));
            if (e instanceof ChangeModelParameterEvent) {
                ChangeModelParameterEvent ev = (ChangeModelParameterEvent) e;
                c.setScale(ev.getNewScale());
                c.setFakeLighting(ev.isFakeLightning());
                c.setIsTransparent(ev.isTransparent());
                c.setNumberOfRows(ev.getNumColums());
                c.setReflectivity(ev.getReflectivity());
                c.setShineDamper(ev.getShineDamper());
            } else if (e instanceof NewModelOrTextureEvent) {
                NewModelOrTextureEvent event = (NewModelOrTextureEvent) e;
                if (event.getModelFile() != null && event.getTextureFile() != null) {
                    c.loadModel(event.getModelFile(), event.getTextureFile());
                } else if (event.getModelFile() != null) {
                    c.loadModel(event.getModelFile(), null);
                } else {
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
                if (hasComponent(e.getEntityId(), PositionComponent.class)) {
                    PositionComponent pos = getComponent(e.getEntityId(), PositionComponent.class);
                    getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                        pos.getPosition(), createNewLightEvent.getLight());
                } else {
                    System.out.println("3");
                    getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                        new Vector3f(0, 0, 0), createNewLightEvent.getLight());
                }
            }
        }
        if (e instanceof RemoveLightEvent && hasComponent(e.getEntityId(), EmittingLightComponent.class)) {
            getComponent(e.getEntityId(), EmittingLightComponent.class).removeLight(((RemoveLightEvent) e).getLight());
        }
    }

    @Override
    public void update() {
        Set<Long> entities = EntityManager.getAllEntites();
        for (Long entity : entities) {
            if (hasComponent(entity, EmittingLightComponent.class)
                && hasComponent(entity, PositionComponent.class)) {
                EmittingLightComponent c = getComponent(entity, EmittingLightComponent.class);
                PositionComponent pos = getComponent(entity, PositionComponent.class);
                if (c != null && pos != null) {
                    c.updateLightPositionToEntity(entity, pos.getPosition());
                }
            }
        }
    }

    private boolean hasComponent(Long entity, Class<? extends Component> clazz) {
        return EntityManager.hasComponent(entity, clazz);
    }

    public Scene createScene() {
        Set<Long> entities = EntityManager.getAllEntites();
        Scene scene = new Scene();
        for (Long entity : entities) {
            Map<String, Component> allComponents = EntityManager.getAllComponents(entity);
            for (Component c : allComponents.values()) {
                if (c instanceof GraphicalComponent) {
                    GraphicalComponent gc = (GraphicalComponent) c;
                    gc.render(entity, scene);
                }
                if (c instanceof PositionComponent) {
                    PositionComponent pc = (PositionComponent) c;
                    scene.getPositions().put(entity, pc.getPosition());
                    scene.getRotations().put(entity, pc.getRotation());
                }
            }
        }
        return scene;
    }
}
