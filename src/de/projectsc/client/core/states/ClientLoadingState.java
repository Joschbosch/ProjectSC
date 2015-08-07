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
 * State when loading all data for a started game.
 * 
 * @author Josch Bosch
 */
public class ClientLoadingState extends ClientGameState {

    private static final Log LOGGER = LogFactory.getLog(ClientLoadingState.class);

    private ClientGameContext context;

    @Override
    public void call(ClientGameContext gameContext) throws Exception {
        this.context = gameContext;
        LOGGER.debug("Entered game state " + gameContext.getState());
        new Thread(new Runnable() {

            @Override
            public void run() {}
        }).start();
        context.getCore().changeState(this);

    }

    @Override
    public void handleMessage(ClientMessage msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessages(List<ClientMessage> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loop(long tickTime) {
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
