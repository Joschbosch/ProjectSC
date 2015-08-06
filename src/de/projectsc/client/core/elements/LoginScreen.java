/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.client.core.elements;

/**
 * Screen if client wants to login.
 * 
 * @author Josch Bosch
 */
public class LoginScreen implements UIElement {

    private String loginMessage = "";

    public void setLoginMessage(String msg) {
        loginMessage = msg;
    }

    public String getLoginMessage() {
        return loginMessage;
    }
}
