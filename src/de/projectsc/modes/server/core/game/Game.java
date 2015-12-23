/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.server.core.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.component.impl.ComponentListItem;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.game.GameAttributes;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.messages.MessageConstants;
import de.projectsc.core.systems.physics.PhysicsSystem;
import de.projectsc.modes.server.core.ServerCommands;
import de.projectsc.modes.server.core.ServerConstants;
import de.projectsc.modes.server.core.data.AuthenticatedClient;
import de.projectsc.modes.server.core.game.data.ServerPlayer;
import de.projectsc.modes.server.core.messages.ServerMessage;

/**
 * 
 * The actual game, first as lobby, than as the game.
 * 
 * @author Josch Bosch
 */
public class Game implements Runnable {

    public static final long GAME_TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(Game.class);

    private static final int ID_START = 1000;

    private static final Object LOCK_OBJECT = new Object();

    private static int idCounter = ID_START;

    private final AtomicBoolean lobbyAlive = new AtomicBoolean(true);

    private final BlockingQueue<ServerMessage> coreQueue;

    private States currentState;

    private final GameContext gameContext;

    private Map<Long, Byte> playerLoadingProgress;

    private boolean startLoading = false;

    private boolean loading = false;

    private PhysicsSystem physicsSystem;

    public Game(AuthenticatedClient host, BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
        this.gameContext = new GameContext(idCounter++, host.getDisplayName() + "'s game", new ServerPlayer(host), this);
        LOGGER.debug(String.format("Created new game: %s (Host: %s, game id: %d)", gameContext.getDisplayName(), host.getDisplayName(),
            gameContext.getGameID()));
        new Thread(this).start();
    }

    @Override
    public void run() {
        Timer.init();
        LOGGER.debug(String.format("Game %d started", gameContext.getGameID()));
        while (lobbyAlive.get()) {
            Timer.update();
            readMessages();
            while (Timer.getLag() >= GAME_TICK_TIME) {
                if (currentState == States.LOBBY) {
                    loopLobby();
                } else if (currentState == States.LOADING) {
                    loopLoading();
                } else if (currentState == States.RUNNING) {
                    loopGame();
                }
                Timer.setLag(Timer.getLag() - GAME_TICK_TIME);
            }
            long timeNeeded = System.currentTimeMillis() - Timer.getSnapshotTime();
            long sleepTime = Math.max((GAME_TICK_TIME - timeNeeded), 0L);
            // LOGGER.debug(
            // String.format("Game %d needed %d ms for current tick, will sleep : %d",
            // gameContext.getGameID(), timeNeeded, sleepTime));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.debug(e);
            }
        }
        LOGGER.debug(String.format("Game %d terminated", gameContext.getGameID()));
    }

    private void loopGame() {
        // TODO Auto-generated method stub

    }

    private void loopLoading() {
        if (startLoading) {
            playerLoadingProgress = new TreeMap<>();
            for (Long id : gameContext.getPlayers().keySet()) {
                playerLoadingProgress.put(id, new Byte((byte) 0));
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    physicsSystem = new PhysicsSystem();
                    loadComponents();
                    gameContext.loadData();
                }
            }).start();
            loading = true;
            startLoading = false;
        }
        if (loading) {
            int sumLoading = gameContext.getLoadingProgress();
            for (Long id : playerLoadingProgress.keySet()) {
                sumLoading += playerLoadingProgress.get(id);
            }
            int percentageLoaded = sumLoading / (playerLoadingProgress.size() + 1);
            final int maximumpercentage = 100;
            if (percentageLoaded >= maximumpercentage) {
                LOGGER.debug("Loading done! Starting game!");
                gameContext.setLoading(false);
                // finishedLoading
                loading = false;
            }
        }
    }

    private void loadComponents() {
        for (ComponentListItem it : ComponentListItem.values()) {
            ComponentManager.registerComponent(it.getName(), it.getClazz());
        }
    }

    private void loopLobby() {
        // TODO Auto-generated method stub

    }

    private void readMessages() {
        List<ServerPlayer> toRemove = new LinkedList<>();
        List<ServerPlayer> toQuit = new LinkedList<>();

        Map<Long, ServerPlayer> players = gameContext.getPlayers();
        for (ServerPlayer player : players.values()) {
            BlockingQueue<ServerMessage> queue = player.getClient().getReceiveFromClientQueue();
            while (!queue.isEmpty()) {
                ServerMessage msg = queue.poll();
                if (msg.getMessage().equals(MessageConstants.CHAT_MESSAGE)) {
                    sendMessageToAllPlayer(new ServerMessage(msg.getMessage(), msg.getData()[0], player.getDisplayName()));
                } else if (msg.getMessage().equals(ServerCommands.LIST_PLAYER)) {
                    createPlayerList();
                } else if (msg.getMessage().equals(MessageConstants.PLAYER_QUIT_LOBBY_REQUEST)) {
                    if (players.containsKey(player.getId())) {
                        toQuit.add(player);
                        sendMessageToAllPlayer(new ServerMessage(MessageConstants.PLAYER_QUIT_LOBBY_REQUEST, player.getId(),
                            player.getDisplayName()));
                        LOGGER.debug(String.format("Player left game %s: %s (Game %s)", gameContext.getDisplayName(),
                            player.getDisplayName(), gameContext.getGameID()));

                    }
                } else if (msg.getMessage().equals(MessageConstants.CLIENT_DISCONNECTED)) {
                    if (players.containsKey(player.getId())) {
                        toRemove.add(player);

                        sendMessageToAllPlayer(new ServerMessage(MessageConstants.CLIENT_DISCONNECTED, player.getId(),
                            player.getDisplayName()));
                        LOGGER.debug(String.format("Player disconnected from game %s: %s (Game %s)", gameContext.getDisplayName(),
                            player.getDisplayName(),
                            gameContext.getGameID()));

                    }
                } else if (currentState == States.LOBBY) {
                    handleMessagesLobby(player, msg);
                } else if (currentState == States.LOADING) {
                    handleMessagesLoading(player, msg);
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

    private void handleMessagesLoading(ServerPlayer player, ServerMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.UPDATE_LOADING_PROGRESS)) {
            playerLoadingProgress.put(player.getId(), Byte.parseByte((String) msg.getData()[0]));
            LOGGER.debug("Map loading completed " + gameContext.getLoadingProgress() + "%");
            int sumLoading = gameContext.getLoadingProgress();
            for (Long id : playerLoadingProgress.keySet()) {
                sumLoading += playerLoadingProgress.get(id);
            }
            int percentageLoaded = sumLoading / (playerLoadingProgress.size() + 1);
            LOGGER.debug("Overall loading completed " + percentageLoaded + "%");

        }

    }

    private void handleMessagesLobby(ServerPlayer player, ServerMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.START_GAME_REQUEST)) {
            String reason = checkIfGameIsStartAble(player);
            if (reason.isEmpty()) {
                sendMessageToAllPlayer(new ServerMessage(GameMessageConstants.START_GAME));
                this.currentState = States.LOADING;
                startLoading = true;
            } else {
                player.getClient().getSendToClientQueue().offer(new ServerMessage(GameMessageConstants.ERROR_STARTING_GAME, reason));
            }
        }
    }

    private String checkIfGameIsStartAble(ServerPlayer player) {
        String response = "";
        if (!gameContext.getHost().getId().equals(player.getId())) {
            response = "Error: Player starting game ist not host";
        }
        return response;
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
        coreQueue.offer(new ServerMessage(MessageConstants.PLAYER_QUIT_LOBBY_REQUEST, player.getClient()));
    }

    /**
     * 
     * New player joins the lobby. Hooray!
     * 
     * @param newPlayer that joined.
     */
    public void addPlayerToGameLobby(AuthenticatedClient newPlayer) {
        ServerPlayer player = new ServerPlayer(newPlayer);
        sendMessageToAllPlayer(new ServerMessage(MessageConstants.PLAYER_JOINED_GAME, player.getId(), player.getDisplayName()));
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

    public long getGameID() {
        return gameContext.getGameID();
    }

    public String getDisplayName() {
        return gameContext.getDisplayName();
    }

    public boolean isFull() {
        return false;
    }

    /**
     * Is the current lobby joinable?
     * 
     * @return true if so.
     */
    public String isJoinable() {
        String returnString = "";
        if (!currentState.equals(States.LOBBY)) {
            returnString = "Game already running";
        }
        return returnString;
    }

    public GameConfiguration getConfiguration() {
        return gameContext.getConfig();
    }

    public States getCurrentState() {
        return currentState;
    }
}
