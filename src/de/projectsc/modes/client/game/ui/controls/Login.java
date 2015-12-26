/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.game.ui.controls;

import de.projectsc.modes.client.core.ui.UIElement;

/**
 * Screen if client wants to login.
 * 
 * @author Josch Bosch
 */
public class Login extends UIElement {

    private String loginMessage = "";

    public Login() {
        super("Login", 0);
    }

    public void setLoginMessage(String msg) {
        loginMessage = msg;
    }

    public String getLoginMessage() {
        return loginMessage;
    }
}
