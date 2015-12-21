/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.messages.MessageConstants;
import de.projectsc.modes.server.core.data.AuthenticatedClient;
import de.projectsc.modes.server.core.data.connections.messages.NewClientConnectedServerMessage;
import de.projectsc.modes.server.core.game.Game;
import de.projectsc.modes.server.core.messages.ServerMessage;

/**
 * Core of server.
 * 
 * @author Josch Bosch
 */
public class ServerCore implements Runnable {

    private static final String CORE_ERROR = "Core Error: ";

    private static final Log LOGGER = LogFactory.getLog(ServerCore.class);

    private static AtomicBoolean shutdown = new AtomicBoolean(false);

    private final BlockingQueue<ServerMessage> receiveQueue;

    private final Map<Long, AuthenticatedClient> clientsInMainLobby;

    private final Map<Long, Game> games;

    private ServerConsole console;

    public ServerCore() {
        receiveQueue = new LinkedBlockingQueue<>();
        clientsInMainLobby = new TreeMap<>();
        games = new TreeMap<>();
    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");
        console = new ServerConsole(receiveQueue);
        console.hashCode();
        while (!shutdown.get()) {
            try {
                Thread.sleep(ServerConstants.SLEEPTIME);
            } catch (InterruptedException e) {
                LOGGER.error(CORE_ERROR, e);
            }
            workMessages();
        }
        LOGGER.debug("Server core shut down.");
    }

    private void workMessages() {
        while (!receiveQueue.isEmpty()) {
            ServerMessage msg = receiveQueue.poll();
            handleServerInternalMessages(msg);
        }
        List<AuthenticatedClient> toRemove = new LinkedList<>();

        for (AuthenticatedClient client : clientsInMainLobby.values()) {
            while (!client.getReceiveFromClientQueue().isEmpty()) {
                ServerMessage msg = client.getReceiveFromClientQueue().poll();
                handleClientMessage(client, msg, toRemove);
            }
        }

        for (AuthenticatedClient c : toRemove) {
            clientsInMainLobby.remove(c.getId());
        }

        List<Long> gamesToRemove = new LinkedList<>();
        for (Game g : games.values()) {
            if (!g.isAlive() || g.getPlayerCount() == 0) {
                g.shutdown();
                gamesToRemove.add(g.getGameID());
            }
        }
        for (Long id : gamesToRemove) {
            games.remove(id);
        }
    }

    private void handleClientMessage(AuthenticatedClient client, ServerMessage msg, List<AuthenticatedClient> toRemove) {
        if (msg.getMessage().equals(MessageConstants.CHAT_MESSAGE)) {
            receiveQueue.offer(new ServerMessage(msg.getMessage(), msg.getData()[0], clientsInMainLobby.get(client.getId())
                .getDisplayName()));
        } else if (msg.getMessage().equals(MessageConstants.CREATE_NEW_GAME_REQUEST)) {
            Game newGame = new Game(client, receiveQueue);
            games.put(newGame.getGameID(), newGame);
            toRemove.add(client);
            sendMsgToAllClients(new ServerMessage(MessageConstants.CLIENT_LEFT_LOBBY, client.getId(), client.getDisplayName()));
            client.sendMessage(new ServerMessage(MessageConstants.NEW_GAME_CREATED, newGame.getConfiguration()));
        } else if (msg.getMessage().equals(MessageConstants.JOIN_GAME_REQUEST) && msg.getData() != null && msg.getData().length > 0) {
            try {
                Long game = Long.valueOf(((String) msg.getData()[0]).trim());

                if (games.containsKey(game)) {
                    String joinable = games.get(game).isJoinable();
                    if (joinable.isEmpty()) {
                        games.get(game).addPlayerToGameLobby(client);
                        client.sendMessage(new ServerMessage(MessageConstants.JOIN_GAME_SUCCSESSFULL));
                        toRemove.add(client);
                        sendMsgToAllClients(new ServerMessage(MessageConstants.CLIENT_LEFT_LOBBY, client.getId(),
                            client.getDisplayName()));
                    } else {
                        client.sendMessage(new ServerMessage(MessageConstants.ERROR_JOINING_GAME, String.format(
                            joinable)));
                    }
                } else {
                    client.sendMessage(new ServerMessage(MessageConstants.ERROR_JOINING_GAME, String.format(
                        "Game with id %s does not exist", game)));
                }
            } catch (NumberFormatException e) {
                client.sendMessage(new ServerMessage(MessageConstants.ERROR_JOINING_GAME, String.format(
                    "Id %s is not valid", msg.getData()[0])));
            }
        } else if (msg.getMessage().equals(MessageConstants.CLIENT_DISCONNECTED)) {
            // handle id not here
            if (clientsInMainLobby.containsKey(client.getId())) {
                AuthenticatedClient c = clientsInMainLobby.remove(client.getId());
                sendMsgToAllClients(new ServerMessage(MessageConstants.CLIENT_DISCONNECTED, c.getId(), c.getDisplayName()));
            }
        }
    }

    private void handleServerInternalMessages(ServerMessage msg) {
        if (msg.getMessage().equals(MessageConstants.SHUTDOWN)) {
            for (Game g : games.values()) {
                g.shutdown();
            }
            sendMsgToAllClients(msg);
            shutdown.set(true);
        } else if (msg.getMessage().equals(MessageConstants.CHAT_MESSAGE)) {
            sendMsgToAllClients(msg);
        } else if (msg.getMessage().equals(MessageConstants.NEW_CLIENT_CONNECTED)) {
            LOGGER.debug("New Client connected to server!");
            NewClientConnectedServerMessage newClient = (NewClientConnectedServerMessage) msg;
            sendMsgToAllClients(new ServerMessage(MessageConstants.CLIENT_JOINED_LOBBY, newClient.getClient().getId(), newClient
                .getClient().getDisplayName()));
            clientsInMainLobby.put(newClient.getClient().getId(), newClient.getClient());
        } else if (msg.getMessage().equals(MessageConstants.CLIENT_DISCONNECTED)) {
            Long id = Long.parseLong((String) msg.getData()[0]);
            // handle id not here
            if (clientsInMainLobby.containsKey(id)) {
                AuthenticatedClient c = clientsInMainLobby.remove(id);
                sendMsgToAllClients(new ServerMessage(MessageConstants.CLIENT_DISCONNECTED, c.getId(), c.getDisplayName()));
            }
        } else if (msg.getMessage().equals(MessageConstants.PLAYER_QUIT_LOBBY_REQUEST)) {
            AuthenticatedClient client = (AuthenticatedClient) msg.getData()[0];
            sendMsgToAllClients(new ServerMessage(MessageConstants.CLIENT_JOINED_LOBBY, client.getId(), client.getDisplayName()));
            clientsInMainLobby.put(client.getId(), client);
            client.sendMessage(new ServerMessage(MessageConstants.JOIN_LOBBY));
        } else if (msg.getMessage().equals(ServerCommands.LISTCLIENTS)) {
            createClientList();
        } else if (msg.getMessage().equals(ServerCommands.LIST_GAMES)) {
            createGameList();
        } else {
            LOGGER.debug("Message not recognized: " + msg.getMessage());
        }
    }

    private void createClientList() {
        String clientList = "\nClients in main lobby: \n";
        if (clientsInMainLobby.size() == 0) {
            clientList = "\nNo clients in the lobby";
        } else {
            for (Long id : clientsInMainLobby.keySet()) {
                clientList += String.format("(%d) %s\n", id, clientsInMainLobby.get(id).getDisplayName());
            }
        }
        LOGGER.debug(clientList);
    }

    private void createGameList() {
        String gameList = "\nOpen game lobbies: \n";
        if (games.size() == 0) {
            gameList = "\nNo games";
        } else {
            for (Long id : games.keySet()) {
                gameList +=
                    String.format("(%4d) %s (%d player) %s\n", id, games.get(id).getDisplayName(), games.get(id).getPlayerCount(), games
                        .get(id).getGameState());
            }
        }
        LOGGER.debug(gameList);
    }

    private void sendMsgToAllClients(ServerMessage msg) {
        for (AuthenticatedClient client : clientsInMainLobby.values()) {
            client.sendMessage(msg);
        }
    }

    public BlockingQueue<ServerMessage> getReceiveQueue() {
        return receiveQueue;
    }

    public boolean isAlive() {
        return !shutdown.get();
    }
}
