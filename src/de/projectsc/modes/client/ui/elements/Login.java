/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.ui.elements;

import de.projectsc.modes.client.core.states.StateConstants;
import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * Screen if client wants to login.
 * 
 * @author Josch Bosch
 */
public class Login extends BasicUIElement {

    private String loginMessage = "";

    public Login() {
        super(StateConstants.LOGIN, 0);
    }

    public void setLoginMessage(String msg) {
        loginMessage = msg;
    }

    public String getLoginMessage() {
        return loginMessage;
    }
}
