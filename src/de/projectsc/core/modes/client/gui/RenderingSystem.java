/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui;

import java.util.Map;
import java.util.Set;

import de.projectsc.core.EngineSystem;
import de.projectsc.core.EntityManager;
import de.projectsc.core.EventManager;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.events.ChangeScaleEvent;
import de.projectsc.core.entities.Component;
import de.projectsc.core.modes.client.gui.components.GraphicalComponent;
import de.projectsc.core.modes.client.gui.components.ModelAndTextureComponent;
import de.projectsc.core.modes.client.gui.data.Scene;
import de.projectsc.core.systems.localisation.events.MoveEvent;
import de.projectsc.core.systems.localisation.events.NewPositionEvent;

public class RenderingSystem extends EngineSystem {

    private static final String NAME = "Rendering System";

    public RenderingSystem() {
        super(NAME);
        EventManager.registerForEvent(NewPositionEvent.class, this);
        EventManager.registerForEvent(ChangeScaleEvent.class, this);
        EventManager.registerForEvent(MoveEvent.class, this);

    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof NewPositionEvent) {
            Map<String, Component> allComponents = EntityManager.getAllComponents(e.getEntityId());
            for (Component c : allComponents.values()) {
                if (c instanceof GraphicalComponent) {
                    GraphicalComponent gc = (GraphicalComponent) c;
                    gc.setNewPosition(((NewPositionEvent) e).getNewPosition(), ((NewPositionEvent) e).getNewRotation());
                }
            }
        } else if (e instanceof ChangeScaleEvent) {
            ModelAndTextureComponent c =
                ((ModelAndTextureComponent) EntityManager.getComponent(e.getEntityId(), ModelAndTextureComponent.NAME));
            c.setScale(((ChangeScaleEvent) e).getNewScale());
        }
    }

    @Override
    public void update() {

    }

    public Scene createScene() {
        Set<Long> entities = EntityManager.getAllEntites();
        Scene scene = new Scene();
        for (Long entity : entities) {
            Map<String, Component> allComponents = EntityManager.getAllComponents(entity);
            for (Component c : allComponents.values()) {
                if (c instanceof GraphicalComponent) {
                    GraphicalComponent gc = (GraphicalComponent) c;
                    gc.render(scene);
                }
            }
        }
        return scene;
    }
}
