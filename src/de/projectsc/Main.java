/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import de.projectsc.core.Core;
import de.projectsc.gui.GUICore;

/**
 * Main class.
 * 
 * @author Josch Bosch
 */
public class Main {

    protected Main() {

    }

    /**
     * Start of the game.
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        Core core = new Core();
        final GUICore gui = new GUICore(core.getGuiIncomingQueue(), core.getGuiOutgoingQueue());
        new Thread(core).start();
        new Thread(gui).start();
    }
}
