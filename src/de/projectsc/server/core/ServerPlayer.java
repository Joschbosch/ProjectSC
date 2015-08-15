/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.server.core;

<<<<<<< HEAD
import de.projectsc.core.entities.PlayerEntity;
import de.projectsc.server.core.client.AuthenticatedClient;
=======
import de.projectsc.core.entities.Entity;
>>>>>>> 0d815c6d3e0afc9dc3b2445bc4ea6c5660f8496f

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
