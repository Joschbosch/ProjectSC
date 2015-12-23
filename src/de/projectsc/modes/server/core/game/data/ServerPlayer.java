/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.core.game.data;

import de.projectsc.core.interfaces.Entity;
import de.projectsc.modes.server.core.data.AuthenticatedClient;

/**
 * Representation of a player in the lobby of one game and later in the game.
 * 
 * @author Josch Bosch
 */
public class ServerPlayer {

    private final AuthenticatedClient client;

    private final long id;

    private Entity entity;

    public ServerPlayer(AuthenticatedClient client) {
        this.client = client;
        this.id = client.getId();
    }

    public AuthenticatedClient getClient() {
        return client;
    }

    public Entity getEntity() {
        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return client.getDisplayName();
    }

    public void setEntity(Entity newEntity) {
        this.entity = newEntity;
    }
}
