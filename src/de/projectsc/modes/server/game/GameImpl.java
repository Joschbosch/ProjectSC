/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.server.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.registry.ComponentListItem;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.structure.SnapshotDelta;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.events.entity.actions.MoveEntityToTargetAction;
import de.projectsc.core.game.GameAttributes;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.game.systems.CombatSystem;
import de.projectsc.core.game.systems.HealthSystem;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.messages.MessageConstants;
import de.projectsc.core.systems.physics.BasicPhysicsSystem;
import de.projectsc.core.systems.physics.collision.CollisionSystem;
import de.projectsc.core.systems.state.EntityStateSystem;
import de.projectsc.core.utils.MapLoader;
import de.projectsc.modes.server.core.ServerCommands;
import de.projectsc.modes.server.core.ServerConstants;
import de.projectsc.modes.server.core.data.AuthenticatedClient;
import de.projectsc.modes.server.core.data.States;
import de.projectsc.modes.server.core.manager.ServerSnapshotManager;
import de.projectsc.modes.server.core.messages.ServerMessage;
import de.projectsc.modes.server.core.spi.Game;
import de.projectsc.modes.server.game.ai.AISystem;
import de.projectsc.modes.server.game.data.ServerPlayer;

/**
 * 
 * The actual game, first as lobby, than as the game.
 * 
 * @author Josch Bosch
 */
public class GameImpl implements Game {

    /**
     * Tick time on the server-.
     */
    public static final long GAME_TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(GameImpl.class);

    private static final int ID_START = 1000;

    private static int idCounter = ID_START;

    private final AtomicBoolean lobbyAlive = new AtomicBoolean(true);

    private final BlockingQueue<ServerMessage> coreQueue;

    private States currentState;

    private final GameContext gameContext;

    private Map<String, Byte> playerLoadingProgress;

    private boolean startLoading = false;

    private boolean loading = false;

    private BasicPhysicsSystem physicsSystem;

    private EntityStateSystem stateSystem;

    private CollisionSystem collisionSystem;

    private AISystem aiSystem;

    private CombatSystem fightSystem;

    private HealthSystem healthSystem;

    private ComponentManager componentManager;

    private EntityManager entityManager;

    private EventManager eventManager;

    private ServerSnapshotManager snapshotManager;

    private AtomicBoolean loadingDone = new AtomicBoolean(false);

    private Timer timer;

    public GameImpl(AuthenticatedClient host, BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
        this.gameContext = new GameContext(idCounter++, host.getDisplayName() + "'s game", new ServerPlayer(host), this);
        LOGGER.info(String.format("Created new game: %s (Host: %s, game id: %d)", gameContext.getDisplayName(), host.getDisplayName(),
            gameContext.getGameID()));
        this.currentState = States.LOBBY;
        timer = new Timer();
        new Thread(this).start();
    }

    @Override
    public void run() {
        timer.init();
        LOGGER.info(String.format("Game %d started", gameContext.getGameID()));
        while (lobbyAlive.get()) {
            timer.update();
            readMessages();
            while (timer.getLag() >= GAME_TICK_TIME) {
                if (currentState == States.LOBBY) {
                    loopLobby();
                } else if (currentState == States.LOADING) {
                    loopLoading();
                } else if (currentState == States.RUNNING) {
                    loopGame();
                }
                timer.setLag(timer.getLag() - GAME_TICK_TIME);
            }
            long timeNeeded = System.currentTimeMillis() - timer.getSnapshotTime();
            long sleepTime = Math.max((GAME_TICK_TIME - timeNeeded), 0L);
            if (sleepTime != 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    LOGGER.info(e);
                }
            }
        }
        LOGGER.info(String.format("Game %d terminated", gameContext.getGameID()));
    }

    private void loopGame() {
        timer.updateGameTimeAndTick(GAME_TICK_TIME);
        stateSystem.update(GAME_TICK_TIME);
        physicsSystem.update(GAME_TICK_TIME);
        aiSystem.update(GAME_TICK_TIME);
        fightSystem.update(GAME_TICK_TIME);
        healthSystem.update(GAME_TICK_TIME);
        collisionSystem.update(GAME_TICK_TIME);
        snapshotManager.createSnapshot(timer);
        for (ServerPlayer player : gameContext.getPlayers().values()) {
            long lastSendSnapshotTick = snapshotManager.getLastSendSnapshotTick(player.getId());
            if (lastSendSnapshotTick == -1) {
                sendFullSnapshotToClient(player);
            } else if (timer.getTick() - lastSendSnapshotTick > 1) {
                sendSnapshotDelta(player, lastSendSnapshotTick);
            }
        }
    }

    private void sendSnapshotDelta(ServerPlayer player, long lastTick) {
        Snapshot lastSnapshotSend = snapshotManager.getSnapshot(lastTick);
        Snapshot currentSnapshot = snapshotManager.getLastSnapshot();
        SnapshotDelta sd = snapshotManager.createSnapshotDelta(lastSnapshotSend, currentSnapshot);
        player.getClient().sendMessage(new ServerMessage(GameMessageConstants.NEW_SNAPSHOT_DELTA, sd));
        snapshotManager.setLastSnapshotSendTick(player.getId(), sd.getTick());
    }

    private void sendFullSnapshotToClient(ServerPlayer player) {
        Snapshot s = snapshotManager.getLastSnapshot();
        snapshotManager.setLastSnapshotSendTick(player.getId(), s.getTick());
        player.getClient().sendMessage(new ServerMessage("FullSnapshot", s));
    }

    private void loopLoading() {
        if (startLoading) {
            playerLoadingProgress = new TreeMap<>();
            for (String id : gameContext.getPlayers().keySet()) {
                playerLoadingProgress.put(id, new Byte((byte) 0));
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    eventManager = new EventManager();
                    componentManager = new ComponentManager();
                    entityManager = new EntityManager(componentManager, eventManager);
                    stateSystem = new EntityStateSystem(entityManager, eventManager);
                    physicsSystem = new BasicPhysicsSystem(entityManager, eventManager);
                    collisionSystem = new CollisionSystem(entityManager, eventManager);
                    aiSystem = new AISystem(entityManager, eventManager, collisionSystem);
                    fightSystem = new CombatSystem(entityManager, eventManager);
                    healthSystem = new HealthSystem(entityManager, eventManager);
                    snapshotManager = new ServerSnapshotManager(entityManager);
                    loadComponents();
                    gameContext.loadData();
                    MapLoader.loadMap(gameContext.getConfig().getMapName(), entityManager);
                    loadingDone.set(true);
                }
            }).start();
            loading = true;
            startLoading = false;
        }
        if (loading) {
            int sumLoading = gameContext.getLoadingProgress();
            for (String id : playerLoadingProgress.keySet()) {
                sumLoading += playerLoadingProgress.get(id);
            }
            int percentageLoaded = sumLoading / (playerLoadingProgress.size() + 1);
            final int maximumpercentage = 100;
            if (percentageLoaded >= maximumpercentage && loadingDone.get()) {
                LOGGER.info("Loading done! Starting game!");
                gameContext.setLoading(false);
                // finishedLoading
                loading = false;
                currentState = States.RUNNING;

            }
        }
    }

    private void loadComponents() {
        for (ComponentListItem it : ComponentListItem.values()) {
            componentManager.registerComponent(it.getName(), it.getClazz());
        }
    }

    private void loopLobby() {
        // TODO Auto-generated method stub

    }

    private void readMessages() {
        List<ServerPlayer> toRemove = new LinkedList<>();
        List<ServerPlayer> toQuit = new LinkedList<>();

        Map<String, ServerPlayer> players = gameContext.getPlayers();
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
                        LOGGER.info(String.format("Player left game %s: %s (Game %s)", gameContext.getDisplayName(),
                            player.getDisplayName(), gameContext.getGameID()));

                    }
                } else if (msg.getMessage().equals(MessageConstants.CLIENT_DISCONNECTED)) {
                    if (players.containsKey(player.getId())) {
                        toRemove.add(player);

                        sendMessageToAllPlayer(new ServerMessage(MessageConstants.CLIENT_DISCONNECTED, player.getId(),
                            player.getDisplayName()));
                        LOGGER.info(String.format("Player disconnected from game %s: %s (Game %s)", gameContext.getDisplayName(),
                            player.getDisplayName(),
                            gameContext.getGameID()));

                    }
                } else if (currentState == States.LOBBY) {
                    handleMessagesLobby(player, msg);
                } else if (currentState == States.LOADING) {
                    handleMessagesLoading(player, msg);
                } else if (currentState == States.RUNNING) {
                    handleMessagesGame(player, msg);
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

    private void handleMessagesGame(ServerPlayer player, ServerMessage msg) {
        if (msg.getMessage().equals("moveToPosition")) {
            @SuppressWarnings("unchecked") Map<String, Double> positionData = (Map<String, Double>) msg.getData()[1];
            Vector3f position =
                new Vector3f(positionData.get("x").floatValue(), positionData.get("y").floatValue(), positionData.get("z").floatValue());
            eventManager.fireEvent(new MoveEntityToTargetAction((String) msg.getData()[0], position));
        }
    }

    private void handleMessagesLoading(ServerPlayer player, ServerMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.UPDATE_LOADING_PROGRESS) && playerLoadingProgress != null) {
            playerLoadingProgress.put(player.getId(), Byte.parseByte((String) msg.getData()[0]));
            LOGGER.info("Map loading completed " + gameContext.getLoadingProgress() + "%");
            int sumLoading = gameContext.getLoadingProgress();
            for (String id : playerLoadingProgress.keySet()) {
                sumLoading += playerLoadingProgress.get(id);
            }
            int percentageLoaded = sumLoading / (playerLoadingProgress.size() + 1);
            LOGGER.info("Overall loading completed " + percentageLoaded + "%");

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
        Map<String, ServerPlayer> players = gameContext.getPlayers();
        for (String id : players.keySet()) {
            gameList +=
                String.format("(%4d) %s Affiliation: %d - Character: %s\n", id, players.get(id).getDisplayName(), gameContext.getConfig()
                    .getPlayerAffiliation(id), gameContext.getConfig().getPlayerCharacter(id));
        }
        LOGGER.info(gameList);
    }

    private void newHost(ServerPlayer remove) {
        Map<String, ServerPlayer> players = gameContext.getPlayers();
        if (players.size() > 0 && remove.getId().equals(gameContext.getHost().getId())) {
            String nextHost = players.keySet().iterator().next();
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
    @Override
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
        LOGGER.info(String.format("Player joined game %s: %s (Game %s)", gameContext.getDisplayName(), player.getDisplayName(),
            gameContext.getGameID()));
    }

    private void sendMessageToAllPlayer(ServerMessage msg) {
        for (ServerPlayer player : gameContext.getPlayers().values()) {
            player.getClient().getSendToClientQueue().offer(msg);
        }
    }

    @Override
    public int getPlayerCount() {
        return gameContext.getPlayers().size();
    }

    @Override
    public boolean isAlive() {
        return lobbyAlive.get();
    }

    /**
     * Shut down lobby.
     */
    @Override
    public void shutdown() {
        sendMessageToAllPlayer(new ServerMessage(MessageConstants.SHUTDOWN));
        gameContext.terminate();
        lobbyAlive.set(false);
    }

    @Override
    public long getGameID() {
        return gameContext.getGameID();
    }

    @Override
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
    @Override
    public String isJoinable() {
        String returnString = "";
        if (!currentState.equals(States.LOBBY)) {
            returnString = "Game already running";
        }
        return returnString;
    }

    @Override
    public GameConfiguration getConfiguration() {
        return gameContext.getConfig();
    }

    @Override
    public States getCurrentState() {
        return currentState;
    }
}
