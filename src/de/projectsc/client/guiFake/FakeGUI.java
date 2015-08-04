/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.client.guiFake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.GUI;
import de.projectsc.client.core.elements.Chat;
import de.projectsc.client.core.elements.LoginScreen;
import de.projectsc.client.core.elements.Snapshot;
import de.projectsc.client.core.elements.UIElement;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.core.states.ClientStates;
import de.projectsc.core.data.messages.MessageConstants;

public class FakeGUI implements GUI {

    private static final Log LOGGER = LogFactory.getLog(FakeGUI.class);

    public static Queue<ClientMessage> input = new LinkedList<>();

    @Override
    public boolean init() {
        System.out.println("init gui state");
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(50);
                        try {
                            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                            String s = bufferRead.readLine();
                            LOGGER.debug("Got mock command: " + s);
                            handleCommand(input, s);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                    }

                }
            }
        }).start();
        return true;
    }

    @Override
    public void render(ClientStates state, long elapsedTime, Snapshot snapshot) {
        if (state == ClientStates.LOBBY || state == ClientStates.GAME_LOBBY) {
            List<UIElement> ui = snapshot.getUIElements();
            for (UIElement e : ui) {
                if (e instanceof Chat) {
                    List<String> chat = ((Chat) e).getLines();
                    for (String line : chat) {
                        LOGGER.debug(line);
                    }
                } else if (e instanceof LoginScreen) {
                    if (!((LoginScreen) e).getLoginMessage().isEmpty()) {
                        LOGGER.debug("Status of login: " + ((LoginScreen) e).getLoginMessage());
                    }
                }
            }
        }
    }

    @Override
    public void changeState(ClientStates state) {
        System.out.println("Change GUI State");
    }

    @Override
    public Queue<ClientMessage> readInput() {
        Queue<ClientMessage> newInput = new LinkedList<>();
        while (!input.isEmpty()) {
            ClientMessage msg = input.poll();
            newInput.add(msg);
        }
        return newInput;
    }

    @Override
    public void load() {
        System.out.println("loading level...");
    }

    private void handleCommand(Queue<ClientMessage> input, String s)
        throws InterruptedException {
        if (s != null && !s.isEmpty()) {
            String[] split = s.split("\\s");
            String inputPrefix = "input:";
            if (split[0].startsWith(inputPrefix)) {
                if (split.length > 1) {
                    input.add(new ClientMessage(split[0].replace(inputPrefix, ""), split[1]));
                } else {
                    input.add(new ClientMessage(split[0].replace(inputPrefix, "")));
                }
            } else if (split[0].equals(MessageConstants.SHUTDOWN)) {
            } else if (split[0].equals("create-client")) {
                if (split.length > 1) {
                    // createNewClient(split, clientQueue);
                } else {
                    LOGGER.debug("Could not create mock client: arguments invalid");
                }

            } else {
            }
        }

    }
}
