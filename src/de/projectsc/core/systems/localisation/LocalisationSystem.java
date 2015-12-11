/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.localisation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.EngineSystem;
import de.projectsc.core.EntityManager;
import de.projectsc.core.EventManager;
import de.projectsc.core.data.Event;
import de.projectsc.core.entities.TransformComponent;
import de.projectsc.core.systems.localisation.events.ChangePositionEvent;
import de.projectsc.core.systems.localisation.events.MoveEvent;
import de.projectsc.core.systems.localisation.events.NewPositionEvent;
import de.projectsc.core.systems.localisation.events.RotateEvent;

public class LocalisationSystem extends EngineSystem {

    private static final String NAME = "Localisation System";

    private static final Log LOGGER = LogFactory.getLog(LocalisationSystem.class);

    public LocalisationSystem() {
        super(LocalisationSystem.NAME);
        EventManager.registerForEvent(ChangePositionEvent.class, this);
        EventManager.registerForEvent(RotateEvent.class, this);
        EventManager.registerForEvent(MoveEvent.class, this);
    }

    @Override
    public void processEvent(Event e) {
        TransformComponent posComp = (TransformComponent) EntityManager.getComponent(e.getEntityId(), TransformComponent.NAME);
        if (e instanceof ChangePositionEvent) {
            handlePositionEvent((ChangePositionEvent) e, posComp);
        } else if (e instanceof RotateEvent) {
            handleRotateEvent((RotateEvent) e, posComp);
        }
    }

    private void handleRotateEvent(RotateEvent e, TransformComponent posComp) {
        if (e.isRelative()) {
            Vector3f.add(posComp.getRotation(), e.getNewRotation(), posComp.getRotation());
        } else if (posComp.getRotation() == null) {
            posComp.setRotation(e.getNewRotation());
        } else {
            posComp.getRotation().set(e.getNewRotation());
        }
        EventManager.fireEvent(new NewPositionEvent(e.getEntityId(), posComp.getPosition(), posComp.getRotation()));
    }

    private void handlePositionEvent(ChangePositionEvent e, TransformComponent posComp) {
        if (e.isRelative()) {
            Vector3f.add(posComp.getRotation(), e.getNewPosition(), posComp.getPosition());
        } else {
            if (posComp.getPosition() == null) {
                posComp.setPosition(e.getNewPosition());
            } else {
                posComp.getPosition().set(e.getNewPosition());
            }
        }
        EventManager.fireEvent(new NewPositionEvent(e.getEntityId(), posComp.getPosition(), posComp.getRotation()));
    }

    @Override
    public void update() {

    }

}
