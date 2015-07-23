/*
 * Copyright (C) 2015
 */

package de.projectsc.client.core.elements;

public class LoginScreen implements UIElement {

    private String loginMessage = "";

    public void setLoginMessage(String msg) {
        loginMessage = msg;
    }

    public String getLoginMessage() {
        return loginMessage;
    }
}
