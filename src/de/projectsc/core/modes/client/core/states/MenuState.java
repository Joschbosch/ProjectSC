/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.core.states;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import de.projectsc.core.modes.client.common.ClientState;
import de.projectsc.core.modes.client.common.StateConstants;
import de.projectsc.core.modes.client.common.messages.ClientMessage;
import de.projectsc.core.modes.client.common.ui.elements.UIElement;
import de.projectsc.core.modes.client.gui.TextMaster;
import de.projectsc.core.modes.client.gui.text.FontType;
import de.projectsc.core.modes.client.gui.text.GUIText;
import de.projectsc.core.utils.Font;
import de.projectsc.core.utils.FontStore;

/**
 * The menu is the state when no game is running. The player can change options, create or join
 * games or do many more things.
 * 
 * @author Josch Bosch
 */
public class MenuState extends ClientState {

    private static final int STATE_LOGIN = 0;

    private static final int STATE_MAIN_MENU = 1;

    private static final int STATE_CREATE_GAME_MENU = 2;

    private static final int STATE_JOIN_GAME_MENU = 3;

    private FontType menuFont;

    private GUIText header;

    private GUIText item1;

    private GUIText item2;

    private GUIText item3;

    private int chosen;

    private final int menuState = STATE_LOGIN;

    @Override
    public void init() {
        menuFont = FontStore.getFont(Font.CANDARA);
        initState(STATE_LOGIN);

    }

    private void initState(int stateLogin) {
        header = TextMaster.createAndLoadText("Project SC", 5f, menuFont, new Vector2f(0, 0f), 1f, true);
        item1 = TextMaster.createAndLoadText("Create Game", 2f, menuFont, new Vector2f(0, 0.4f), 1f, true);
        item2 = TextMaster.createAndLoadText("Join Game", 2f, menuFont, new Vector2f(0, 0.5f), 1f, true);
        item3 = TextMaster.createAndLoadText("Exit", 2f, menuFont, new Vector2f(0, 0.6f), 1f, true);
        chosen = 0;
    }

    @Override
    public void handleMessage(ClientMessage msg) {

    }

    @Override
    public void loop(long tickTime) {
        if (chosen == 0) {
            select(item1);
            unselect(item2);
            unselect(item3);
        } else if (chosen == 1) {
            select(item2);
            unselect(item1);
            unselect(item3);
        } else if (chosen == 2) {
            select(item3);
            unselect(item1);
            unselect(item2);
        }
    }

    private void unselect(GUIText text) {
        text.setBorderWidth(0.0f);
        text.setBorderEdge(0.5f);
        text.setOutlineColor(1.0f, 0, 0);
    }

    private void select(GUIText text) {
        text.setBorderWidth(0.3f);
        text.setBorderEdge(0.5f);
        text.setOutlineColor(1.0f, 0, 0);
    }

    @Override
    public Map<String, List<String>> getGUIObjectsToLoad() {
        Map<String, List<String>> objectToLoad = new HashMap<>();
        List<String> images = new LinkedList<>();
        images.add("images/bg.png");
        objectToLoad.put(StateConstants.IMAGES, images);
        return objectToLoad;
    }

    @Override
    public List<UIElement> getUI() {
        List<UIElement> ui = new LinkedList<>();
        // UITexture bg = new UITexture(Loader.getTextureId("images/bg.png"), new Vector2f(0, 0),
        // new Vector2f(1.1f, 1));
        // ui.add(bg);
        return ui;
    }

    @Override
    public void handleInput(Map<Integer, Integer> keyMap) {
        if (keyMap.get(Keyboard.KEY_DOWN) != null && keyMap.get(Keyboard.KEY_DOWN) == 1) {
            chosen = ++chosen % 3;
        }
        if (keyMap.get(Keyboard.KEY_UP) != null && keyMap.get(Keyboard.KEY_UP) == 1) {
            chosen = --chosen % 3;
        }
    }
}
