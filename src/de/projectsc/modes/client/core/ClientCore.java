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

import de.projectsc.core.component.ComponentListItem;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.data.ClientPlayer;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.interfaces.GUI;
import de.projectsc.modes.client.core.manager.ClientEventManager;
import de.projectsc.modes.client.core.manager.ClientSnapshotManger;
import de.projectsc.modes.client.core.system.ClientControlSystem;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public abstract class ClientCore implements Runnable {

    protected static final int TICK_TIME = 16;

    protected static final Log LOGGER = LogFactory.getLog(ClientCore.class);

    protected BlockingQueue<ClientMessage> networkSendQueue;

    protected BlockingQueue<ClientMessage> networkReceiveQueue;

    protected ClientState currentState;

    protected final ClientPlayer player;

    protected boolean clientRunning;

    protected GUI gui;

    protected ComponentManager componentManager;

    protected EntityManager entityManager;

    protected EventManager eventManager;

    protected ClientControlSystem controlSystem;

    private Timer timer;

    private ClientSnapshotManger snapshotManager;

    public ClientCore() {
        networkSendQueue = new LinkedBlockingQueue<>();
        networkReceiveQueue = new LinkedBlockingQueue<>();
        player = new ClientPlayer();
        timer = new Timer();
        eventManager = new ClientEventManager();
        componentManager = new ComponentManager(eventManager);
        entityManager = new EntityManager(componentManager, eventManager);
        snapshotManager = new ClientSnapshotManger(entityManager, timer);
    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");
        timer.init();
        loadComponents();
        loadSystems();
        gui.init();
        clientRunning = true;
        currentState = getInitialState();
        changeState(currentState);
        LOGGER.debug(String.format("Client started"));
        while (clientRunning) {
            timer.update();
            ClientState newState = null;
            newState = readServerMessages();
            if (currentState != null) {
                gui.readInput();
                while (timer.getLag() >= TICK_TIME) {
                    currentState.loop(TICK_TIME);
                    timer.setLag(timer.getLag() - TICK_TIME);
                }
                Snapshot[] interpolationSnapshots = snapshotManager.getSnapshotsForInterpolation(timer.getGameTime() - 1000);
                if (interpolationSnapshots != null) {
                    long interpolationTime = timer.getGameTime() - interpolationSnapshots[0].getGameTime();
                    gui.render(interpolationSnapshots, interpolationTime);
                } else {
                    gui.render();
                }
            }
            if (newState != null) {
                changeState(newState);
            }
            long timeNeeded = System.currentTimeMillis() - timer.getSnapshotTime();
            long sleepTime = Math.max((TICK_TIME - timeNeeded), 0L);
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

    protected abstract ClientState getInitialState();

    protected abstract void loadSystems();

    private void changeState(ClientState newState) {
        LOGGER.debug("Initialising state " + newState.getId());
        newState.init(networkSendQueue, entityManager, eventManager, componentManager, snapshotManager, timer);
        currentState = newState;
        gui.initState(newState);
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

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public Timer getTimer() {
        return timer;
    }
}
