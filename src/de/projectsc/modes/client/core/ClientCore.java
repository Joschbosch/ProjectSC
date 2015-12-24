/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.component.impl.ComponentListItem;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.interfaces.InputCommandListener;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.manager.InputConsumeManager;
import de.projectsc.core.systems.physics.PhysicsSystem;
import de.projectsc.modes.client.core.data.ClientGameContext;
import de.projectsc.modes.client.core.data.ClientPlayer;
import de.projectsc.modes.client.core.states.MenuState;
import de.projectsc.modes.client.gui.GUICore;
import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.messages.ClientMessage;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public class ClientCore implements Runnable {

    private static final int TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(ClientCore.class);

    private BlockingQueue<ClientMessage> networkSendQueue;

    private BlockingQueue<ClientMessage> networkReceiveQueue;

    private ClientState currentState;

    @SuppressWarnings("unused")
    private final ClientPlayer player;

    private boolean clientRunning;

    private LinkedBlockingQueue<ClientMessage> userInputQueue;

    private GUI gui;

    private PhysicsSystem physicsSystem;

    private ComponentManager componentManager;

    private EntityManager entityManager;

    private EventManager eventManager;

    private InputConsumeManager inputConsumeManager;

    public ClientCore() {
        networkSendQueue = new LinkedBlockingQueue<>();
        networkReceiveQueue = new LinkedBlockingQueue<>();
        userInputQueue = new LinkedBlockingQueue<>();
        player = new ClientPlayer();
        componentManager = new ComponentManager();
        eventManager = new EventManager();
        inputConsumeManager = new InputConsumeManager();
        entityManager = new EntityManager(componentManager, eventManager, inputConsumeManager);

    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");
        Timer.init();
        loadComponents();
        loadSystems();
        gui = new GUICore(componentManager, entityManager, eventManager, inputConsumeManager);
        gui.initCore();
        clientRunning = true;
        currentState = new MenuState();
        changeState(currentState);
        LOGGER.debug(String.format("Client started"));
        while (clientRunning) {
            Timer.update();
            ClientState newState = null;
            newState = readServerMessages();
            if (currentState != null) {
                inputConsumeManager.processInput(gui.readInput());

                while (Timer.getLag() >= TICK_TIME) {
                    physicsSystem.update(TICK_TIME);
                    currentState.loop(TICK_TIME);
                    Timer.setLag(Timer.getLag() - TICK_TIME);
                }
                currentState.loop(Timer.getLag());
                gui.render(currentState);
            }
            if (newState != null) {
                changeState(newState);
            }
            long timeNeeded = System.currentTimeMillis() - Timer.getSnapshotTime();
            long sleepTime = Math.max((16 - timeNeeded), 0L);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.debug(e);
            }
            if (!gui.isRunning()) {
                clientRunning = false;
                System.exit(0);
            }
        }
        LOGGER.debug(String.format("Client terminated"));

    }

    private void changeState(ClientState newState) {
        if (currentState != null) {
            gui.cleanUpState(currentState);
            if (inputConsumeManager != null && currentState instanceof InputCommandListener) {
                inputConsumeManager.removeListener((InputCommandListener) currentState);
            }
        }
        ClientGameContext gameData = new ClientGameContext();
        newState.init(gui, networkSendQueue, entityManager, eventManager, componentManager, gameData);
        currentState = newState;
        if (inputConsumeManager != null && currentState instanceof InputCommandListener) {
            inputConsumeManager.addListener((InputCommandListener) currentState);
        }
    }

    private void loadSystems() {
        physicsSystem = new PhysicsSystem(entityManager, eventManager);
    }

    private void loadComponents() {
        for (ComponentListItem c : ComponentListItem.values()) {
            componentManager.registerComponent(c.getName(), c.getClazz());
        }
    }

    private ClientState readServerMessages() {
        ClientState newState = null;
        if (currentState != null) {
            while (!networkReceiveQueue.isEmpty()) {
                ClientMessage msg = networkReceiveQueue.poll();
                // LOGGER.debug("Got message from server: " + msg);
                newState = currentState.handleMessage(msg);
            }
        }
        return newState;
    }

    /**
     * @param clientMessage to send to server
     */
    public void sendMessageToServer(ClientMessage clientMessage) {
        networkSendQueue.offer(clientMessage);
    }

    public BlockingQueue<ClientMessage> getNetworkSendQueue() {
        return networkSendQueue;
    }

    public void setNetworkSendQueue(BlockingQueue<ClientMessage> networkIncomingQueue) {
        this.networkSendQueue = networkIncomingQueue;
    }

    public BlockingQueue<ClientMessage> getNetworkReceiveQueue() {
        return networkReceiveQueue;
    }

    public void setNetworkReceiveQueue(BlockingQueue<ClientMessage> networkOutgoingQueue) {
        this.networkReceiveQueue = networkOutgoingQueue;
    }

    public LinkedBlockingQueue<ClientMessage> getUserInputQueue() {
        return userInputQueue;
    }

    public void setUserInputQueue(LinkedBlockingQueue<ClientMessage> userInputQueue) {
        this.userInputQueue = userInputQueue;
    }

}
