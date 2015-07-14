/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core;

import de.projectsc.core.entities.PlayerEntity;

/**
 * Representation of a player in the lobby of one game and later in the game.
 * 
 * @author Josch Bosch
 */
public class ServerPlayer {

    private final AuthenticatedClient client;

    private final long id;

    private PlayerEntity entity;

    public ServerPlayer(AuthenticatedClient client) {
        this.client = client;
        this.id = client.getId();
    }

    public AuthenticatedClient getClient() {
        return client;
    }

    public PlayerEntity getEntity() {
        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return client.getDisplayName();
    }

    public void setWorldEntity(PlayerEntity newEntity) {
        this.entity = newEntity;
    }
}
