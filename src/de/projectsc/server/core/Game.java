/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.server.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.FlowBuilder;
import au.com.ds.ef.StateEnum;
import au.com.ds.ef.call.ContextHandler;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.game.GameAttributes;
import de.projectsc.server.core.gamestates.Events;
import de.projectsc.server.core.gamestates.GameRunningState;
import de.projectsc.server.core.gamestates.GameState;
import de.projectsc.server.core.gamestates.LoadingState;
import de.projectsc.server.core.gamestates.LobbyState;
import de.projectsc.server.core.gamestates.States;
import de.projectsc.server.core.messages.GameMessageConstants;
import de.projectsc.server.core.messages.ServerMessage;
import de.projectsc.server.core.messages.ServerMessageConstants;

/**
 * 
 * The actual game, first as lobby, than as the game.
 * 
 * @author Josch Bosch
 */
public class Game implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(Game.class);

    private static final int ID_START = 1000;

    private static final Object LOCK_OBJECT = new Object();

    private static int idCounter = ID_START;

    private final AtomicBoolean lobbyAlive = new AtomicBoolean(true);

    private final BlockingQueue<ServerMessage> coreQueue;

    private GameState currenState;

    private final GameContext gameContext;

    private EasyFlow<GameContext> flow;

    public Game(AuthenticatedClient host, BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
        this.gameContext = new GameContext(idCounter++, host.getDisplayName() + "'s game", new ServerPlayer(host), this);
        createAndBindFlow();
        flow.start(gameContext);
        LOGGER.debug(String.format("Created new game: %s (Host: %s, game id: %d)", gameContext.getDisplayName(), host.getDisplayName(),
            gameContext.getGameID()));
        new Thread(this).start();
    }

    @Override
    public void run() {
        LOGGER.debug(String.format("Game %d started", gameContext.getGameID()));
        while (lobbyAlive.get()) {
            try {
                Thread.sleep(GameRunningState.GAME_TICK);
            } catch (InterruptedException e) {
                LOGGER.debug(e);
            }
            readMessages();
            synchronized (LOCK_OBJECT) {
                if (currenState != null) {
                    currenState.loop();
                }
            }
        }
        LOGGER.debug(String.format("Game %d terminated", gameContext.getGameID()));
    }

    private void readMessages() {
        List<ServerPlayer> toRemove = new LinkedList<>();
        List<ServerPlayer> toQuit = new LinkedList<>();

        Map<Long, ServerPlayer> players = gameContext.getPlayers();
        for (ServerPlayer player : players.values()) {
            BlockingQueue<ServerMessage> queue = player.getClient().getReceiveFromClientQueue();
            while (!queue.isEmpty()) {
                ServerMessage msg = queue.poll();
                if (msg.getMessage().equals(ServerMessageConstants.CHAT_MESSAGE)) {
                    sendMessageToAllPlayer(new ServerMessage(msg.getMessage(), msg.getData()[0], player.getDisplayName()));
                } else if (msg.getMessage().equals(ServerCommands.LIST_PLAYER)) {
                    createPlayerList();
                } else if (msg.getMessage().equals(ServerMessageConstants.PLAYER_QUIT_LOBBY)) {
                    if (players.containsKey(player.getId())) {
                        toQuit.add(player);
                        sendMessageToAllPlayer(new ServerMessage(ServerMessageConstants.PLAYER_QUIT_LOBBY, player.getId(),
                            player.getDisplayName()));
                        LOGGER.debug(String.format("Player left game %s: %s (Game %s)", gameContext.getDisplayName(),
                            player.getDisplayName(), gameContext.getGameID()));

                    }
                } else if (msg.getMessage().equals(ServerMessageConstants.CLIENT_DISCONNECTED)) {
                    if (players.containsKey(player.getId())) {
                        toRemove.add(player);

                        sendMessageToAllPlayer(new ServerMessage(ServerMessageConstants.CLIENT_DISCONNECTED, player.getId(),
                            player.getDisplayName()));
                        LOGGER.debug(String.format("Player disconnected from game %s: %s (Game %s)", gameContext.getDisplayName(),
                            player.getDisplayName(),
                            gameContext.getGameID()));

                    }
                } else if (currenState != null) {
                    currenState.handleMessage(player, msg);
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

    private void createPlayerList() {
        String gameList = "\nPlayer in game: \n";
        Map<Long, ServerPlayer> players = gameContext.getPlayers();
        for (Long id : players.keySet()) {
            gameList +=
                String.format("(%4d) %s Affiliation: %d - Character: %s\n", id, players.get(id).getDisplayName(), gameContext.getConfig()
                    .getPlayerAffiliation(id), gameContext.getConfig().getPlayerCharacter(id));
        }
        LOGGER.debug(gameList);
    }

    private void newHost(ServerPlayer remove) {
        Map<Long, ServerPlayer> players = gameContext.getPlayers();
        if (players.size() > 0 && remove.getId().equals(gameContext.getHost().getId())) {
            Long nextHost = players.keySet().iterator().next();
            gameContext.setHost(players.get(nextHost));
            sendMessageToAllPlayer(new ServerMessage(GameMessageConstants.NEW_HOST, gameContext.getHost().getId(), gameContext.getHost()
                .getDisplayName()));
        } else {
            gameContext.setHost(null);
            lobbyAlive.set(false);
        }
    }

    /**
     * Player quit or disconnected.
     * 
     * @param player to remove
     */
    public void removePlayerFromLobby(ServerPlayer player) {
        gameContext.getPlayers().remove(player.getId());
        coreQueue.offer(new ServerMessage(ServerMessageConstants.PLAYER_QUIT_LOBBY, player.getClient()));
    }

    /**
     * 
     * New player joins the lobby. Hooray!
     * 
     * @param newPlayer that joined.
     */
    public void addPlayerToGameLobby(AuthenticatedClient newPlayer) {
        ServerPlayer player = new ServerPlayer(newPlayer);
        sendMessageToAllPlayer(new ServerMessage(ServerMessageConstants.PLAYER_JOINED_GAME, player.getId(), player.getDisplayName()));
        gameContext.getPlayers().put(player.getId(), player);
        gameContext.getConfig().setPlayerCharacter(player.getId(), "person");
        byte affiliation = 0;
        if (gameContext.getConfig().getAffiliationCount(GameAttributes.AFFILIATION_LIGHT) < ServerConstants.MAXIMUM_PLAYER_PER_GAME / 2) {
            affiliation = GameAttributes.AFFILIATION_LIGHT;
        } else {
            affiliation = GameAttributes.AFFILIATION_DARK;
        }
        gameContext.getConfig().setPlayerAffiliation(player.getId(), affiliation);
        LOGGER.debug(String.format("Player joined game %s: %s (Game %s)", gameContext.getDisplayName(), player.getDisplayName(),
            gameContext.getGameID()));
    }

    private void sendMessageToAllPlayer(ServerMessage msg) {
        for (ServerPlayer player : gameContext.getPlayers().values()) {
            player.getClient().getSendToClientQueue().offer(msg);
        }
    }

    public int getPlayerCount() {
        return gameContext.getPlayers().size();
    }

    public boolean isAlive() {
        return lobbyAlive.get();
    }

    /**
     * Shut down lobby.
     */
    public void shutdown() {
        sendMessageToAllPlayer(new ServerMessage(MessageConstants.SHUTDOWN));
        gameContext.terminate();
        lobbyAlive.set(false);
    }

    public StateEnum getGameState() {
        return gameContext.getState();
    }

    public long getGameID() {
        return gameContext.getGameID();
    }

    public String getDisplayName() {
        return gameContext.getDisplayName();
    }

    public boolean isFull() {
        return false;
    }

    private void createAndBindFlow() {
        flow = FlowBuilder.from(States.LOBBY)
            .transit(FlowBuilder.on(Events.START_GAME_COMMAND).to(States.LOADING)
                .transit(FlowBuilder.on(Events.FINISHED_LOADING).to(States.RUNNING)
                    .transit(FlowBuilder.on(Events.GAME_ENDS).finish(States.FINISHED))));

        flow.whenEnter(States.LOBBY, new LobbyState());
        flow.whenEnter(States.LOADING, new LoadingState());
        flow.whenEnter(States.RUNNING, new GameRunningState());
        flow.whenEnter(States.PAUSED, new ContextHandler<GameContext>() {

            @Override
            public void call(GameContext context) throws Exception {
                LOGGER.debug("Entered game state " + context.getState());
            }
        });
        flow.whenEnter(States.FINISHED, new ContextHandler<GameContext>() {

            @Override
            public void call(GameContext context) throws Exception {
                LOGGER.debug("Entered game state " + context.getState());
            }
        });
    }

    /**
     * Change current gamestate.
     * 
     * @param newState to change to
     */
    public void changeState(GameState newState) {
        synchronized (LOCK_OBJECT) {
            currenState = newState;
            LOGGER.debug("Set current game state to " + gameContext.getState());
        }
    }

    /**
     * Is the current lobby joinable?
     * 
     * @return true if so.
     */
    public String isJoinable() {
        String returnString = "";
        if (!gameContext.getState().equals(States.LOBBY)) {
            returnString = "Game already running";
        }
        return returnString;
    }
}
