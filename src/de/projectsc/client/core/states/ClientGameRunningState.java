/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.states;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientGameContext;
import de.projectsc.client.core.messages.ClientMessage;

/**
 * State when the game is started and running. This is the main state for all the game logic.
 * 
 * @author Josch Bosch
 */
public class ClientGameRunningState extends ClientGameState {

    /**
     * Constant.
     */
    public static final long GAME_TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(ClientLoadingState.class);

    @Override
    public void call(ClientGameContext context) throws Exception {
        LOGGER.debug("Entered game state " + context.getState());

        this.context = context;
        context.getCore().changeState(this);

    }

    @Override
    public void loop(long tickTime) {}

    @Override
    public void handleMessage(ClientMessage msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessages(List<ClientMessage> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readInput() {
        // TODO Auto-generated method stub

    }

    @Override
    public void changeGUI() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(long elapsed, long lag) {
        // TODO Auto-generated method stub

    }
}
