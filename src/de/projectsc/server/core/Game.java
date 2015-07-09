/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.server.core.serverMessages.GameMessageConstants;
import de.projectsc.server.core.serverMessages.ServerMessage;
import de.projectsc.server.core.serverMessages.ServerMessageConstants;

public class Game implements Runnable {

    private static final long GAME_TICK = 15;

    private static final Log LOGGER = LogFactory.getLog(Game.class);

    private static int idCounter = 1000;

    private ServerPlayer host;

    private Map<Long, ServerPlayer> players;

    private AtomicBoolean lobbyAlive = new AtomicBoolean(true);

    private BlockingQueue<ServerMessage> coreQueue;

    private final int gameID;

    private String displayName;

    public Game(AuthenticatedClient host, BlockingQueue<ServerMessage> coreQueue) {
        this.host = new ServerPlayer(host);
        this.players = new ConcurrentHashMap<>();
        players.put(this.host.getId(), this.host);
        new Thread(this).start();
        this.coreQueue = coreQueue;
        this.gameID = idCounter++;
        displayName = host.getDisplayName() + "'s game";
        LOGGER.debug(String.format("Created new game: %s (Host: %s, game id: %d)", displayName, host.getDisplayName(), gameID));
    }

    @Override
    public void run() {
        while (lobbyAlive.get()) {
            try {
                Thread.sleep(GAME_TICK);
            } catch (InterruptedException e) {
                LOGGER.debug(e);
            }
            readMessages();

        }
    }

    private void readMessages() {
        List<ServerPlayer> toRemove = new LinkedList<>();
        List<ServerPlayer> toQuit = new LinkedList<>();

        for (ServerPlayer player : players.values()) {
            BlockingQueue<ServerMessage> queue = player.getClient().getReceiveFromClientQueue();
            while (!queue.isEmpty()) {
                ServerMessage msg = queue.poll();
                if (msg.getMessage().equals(ServerMessageConstants.CHAT_MESSAGE)) {
                    sendMessageToAllPlayer(new ServerMessage(msg.getMessage(), msg.getData()[0], player.getDisplayName()));
                } else if (msg.getMessage().equals(ServerMessageConstants.PLAYER_QUIT_LOBBY)) {
                    if (players.containsKey(player.getId())) {
                        toQuit.add(player);
                        sendMessageToAllPlayer(new ServerMessage(ServerMessageConstants.PLAYER_QUIT_LOBBY, player.getId(),
                            player.getDisplayName()));
                        LOGGER.debug(String.format("Player left game %s: %s (Game %s)", displayName, player.getDisplayName(), gameID));

                    }
                } else if (msg.getMessage().equals(ServerMessageConstants.CLIENT_DISCONNECTED)) {
                    if (players.containsKey(player.getId())) {
                        toRemove.add(player);

                        sendMessageToAllPlayer(new ServerMessage(ServerMessageConstants.CLIENT_DISCONNECTED, player.getId(),
                            player.getDisplayName()));
                        LOGGER.debug(String.format("Player disconnected from game %s: %s (Game %s)", displayName, player.getDisplayName(),
                            gameID));

                    }
                }
            }
        }

        for (ServerPlayer remove : toRemove) {
            players.remove(remove.getId());
            newHost(remove);
        }
        for (ServerPlayer quitter : toQuit) {
            players.remove(quitter.getId());
            removePlayerFromLobby(quitter);
            newHost(quitter);
        }
    }

    private void newHost(ServerPlayer remove) {
        if (players.size() > 0 && remove.getId().equals(host.getId())) {
            Long nextHost = players.keySet().iterator().next();
            host = players.get(nextHost);
            sendMessageToAllPlayer(new ServerMessage(GameMessageConstants.NEW_HOST, host.getId(), host.getDisplayName()));
        } else {
            host = null;
            lobbyAlive.set(false);
        }
    }

    public void removePlayerFromLobby(ServerPlayer player) {
        players.remove(player.getId());
        coreQueue.offer(new ServerMessage(ServerMessageConstants.PLAYER_QUIT_LOBBY, player.getClient()));
    }

    public void addPlayerToGameLobby(AuthenticatedClient newPlayer) {
        ServerPlayer player = new ServerPlayer(newPlayer);
        sendMessageToAllPlayer(new ServerMessage(ServerMessageConstants.PLAYER_JOINED_GAME, player.getId(), player.getDisplayName()));
        players.put(player.getId(), player);
        LOGGER.debug(String.format("Player joined game %s: %s (Game %s)", displayName, player.getDisplayName(),
            gameID));
    }

    private void sendMessageToAllPlayer(ServerMessage msg) {
        for (ServerPlayer player : players.values()) {
            player.getClient().getSendToClientQueue().offer(msg);
        }
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isAlive() {
        return lobbyAlive.get();
    }

    public void shutdown() {
        lobbyAlive.set(false);
    }

    public long getGameID() {
        return gameID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isFull() {
        return false;
    }
}
