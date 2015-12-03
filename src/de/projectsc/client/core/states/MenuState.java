/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.core.states;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import de.projectsc.client.core.data.Scene;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.gui.text.FontType;
import de.projectsc.client.gui.text.GUIText;
import de.projectsc.client.gui.text.TextMaster;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.ui.UITexture;
import de.projectsc.core.utils.Font;
import de.projectsc.core.utils.FontStore;

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

    private int menuState = STATE_LOGIN;

    @Override
    public void init() {
        menuFont = FontStore.getFont(Font.CANDARA);
        initState(STATE_LOGIN);

    }

    private void initState(int stateLogin) {
        header = new GUIText("Project SC", 5f, menuFont, new Vector2f(0, 0f), 1f, true);
        item1 = new GUIText("Create Game", 2f, menuFont, new Vector2f(0, 0.4f), 1f, true);
        item2 = new GUIText("Join Game", 2f, menuFont, new Vector2f(0, 0.5f), 1f, true);
        item3 = new GUIText("Exit", 2f, menuFont, new Vector2f(0, 0.6f), 1f, true);
        TextMaster.loadText(header);
        TextMaster.loadText(item1);
        TextMaster.loadText(item2);
        TextMaster.loadText(item3);
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

    private void unselect(GUIText chosen) {
        chosen.setBorderWidth(0.0f);
        chosen.setBorderEdge(0.5f);
        chosen.setOutlineColor(1.0f, 0, 0);
    }

    private void select(GUIText chosen) {
        chosen.setBorderWidth(0.3f);
        chosen.setBorderEdge(0.5f);
        chosen.setOutlineColor(1.0f, 0, 0);
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
    public Scene getScene() {
        Scene scene = new Scene();
        return scene;
    }

    @Override
    public List<UITexture> getUI() {
        List<UITexture> ui = new LinkedList<>();
        UITexture bg = new UITexture(Loader.getTextureId("images/bg.png"), new Vector2f(0, 0), new Vector2f(1.1f, 1));
        ui.add(bg);
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
