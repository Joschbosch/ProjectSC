/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.modes.client.common.ui.elements;

import de.projectsc.core.modes.client.common.StateConstants;
import de.projectsc.core.modes.client.common.data.UIElement;

/**
 * Screen if client wants to login.
 * 
 * @author Josch Bosch
 */
public class Login extends UIElement {

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
