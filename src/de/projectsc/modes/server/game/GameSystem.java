/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game;

import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.server.game.ai.FollowPathComponent;
import de.projectsc.modes.server.game.elementComponents.SpawnEntitiesComponent;

/**
 * System to handle everything that has to do with the game mechanics. Might have sub systems.
 * 
 * @author Josch Bosch
 */
public class GameSystem extends DefaultSystem {

    private static final String NAME = "Game System";

    public GameSystem(EntityManager entityManager, EventManager eventManager) {
        super(NAME, entityManager, eventManager);
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(SpawnEntitiesComponent.class);
        for (String entity : entities) {
            SpawnEntitiesComponent sec = getComponent(entity, SpawnEntitiesComponent.class);
            double currentTime = sec.getTimer();
            currentTime += tick;
            if (currentTime >= sec.getSpawnIntervall() && sec.getNumberOfSpawns() != 0) {
                spawnEntities(sec, getComponent(entity, TransformComponent.class));
                currentTime -= sec.getSpawnIntervall();
                if (sec.getNumberOfSpawns() > 0) {
                    sec.setNumberOfSpawns(sec.getNumberOfSpawns() - 1);
                }
            }
            sec.setTimer(currentTime);
        }
    }

    private void spawnEntities(SpawnEntitiesComponent sec, TransformComponent sourceTransform) {
        for (Long schemaID : sec.getEntitySpawnLocations().keySet()) {
            for (Vector3f location : sec.getEntitySpawnLocations().get(schemaID)) {
                String spawn = entityManager.createNewEntityFromSchema(schemaID);
                TransformComponent spawnTransform = getComponent(spawn, TransformComponent.class);
                spawnTransform.setPosition(Vector3f.add(sourceTransform.getPosition(), location, null));
                spawnTransform.setRotation(new Vector3f(sourceTransform.getRotation()));
                if (hasComponent(spawn, FollowPathComponent.class)) {
                    FollowPathComponent fpc = getComponent(spawn, FollowPathComponent.class);
                    fpc.setPathIDToFollow(sec.getFollowPathId());
                }
            }
        }
    }

    @Override
    public void processEvent(Event e) {

    }

}
