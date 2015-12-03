/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.client.core.ui.elements;

/**
 * Screen if client wants to login.
 * 
 * @author Josch Bosch
 */
public class LoginScreen extends UIElement {

    private String loginMessage = "";

    public void setLoginMessage(String msg) {
        loginMessage = msg;
    }

    public String getLoginMessage() {
        return loginMessage;
    }
}
