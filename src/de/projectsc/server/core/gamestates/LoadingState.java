/*
 * Copyright (C) 2015
 */

package de.projectsc.server.core.gamestates;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.server.core.GameContext;
import de.projectsc.server.core.ServerPlayer;
import de.projectsc.server.core.messages.GameMessageConstants;
import de.projectsc.server.core.messages.ServerMessage;

public class LoadingState extends GameState {

    private static final Log LOGGER = LogFactory.getLog(LoadingState.class);

    private GameContext context;

    private Map<Long, Byte> playerLoadingProgress;

    @Override
    public void call(GameContext gameContext) throws Exception {
        this.context = gameContext;
        LOGGER.debug("Entered game state " + gameContext.getState());
        playerLoadingProgress = new TreeMap<>();
        for (Long id : context.getPlayers().keySet()) {
            playerLoadingProgress.put(id, new Byte((byte) 0));
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                context.loadData();
            }
        }).start();
        context.getGame().changeState(this);
    }

    @Override
    public void loop() {
        if (context.isLoading()) {
            int sumLoading = context.getLoadingProgress();
            for (Long id : playerLoadingProgress.keySet()) {
                sumLoading += playerLoadingProgress.get(id);
            }
            int percentageLoaded = sumLoading / (playerLoadingProgress.size() + 1);
            if (percentageLoaded >= 100) {
                LOGGER.debug("Loading done! Starting game!");
                context.setLoading(false);
                context.trigger(Events.FINISHED_LOADING);
            }
        }
    }

    @Override
    public void handleMessage(ServerPlayer player, ServerMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.UPDATE_LOADING_PROGRESS)) {
            playerLoadingProgress.put(player.getId(), Byte.parseByte((String) msg.getData()[0]));
            LOGGER.debug("Map loading completed " + context.getLoadingProgress() + "%");
            int sumLoading = context.getLoadingProgress();
            for (Long id : playerLoadingProgress.keySet()) {
                sumLoading += playerLoadingProgress.get(id);
            }
            int percentageLoaded = sumLoading / (playerLoadingProgress.size() + 1);
            LOGGER.debug("Overall loading completed " + percentageLoaded + "%");

        }
    }

    @Override
    public void handleMessages(ServerPlayer player, List<ServerMessage> msg) {

    }
}
