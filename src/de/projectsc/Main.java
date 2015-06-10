package de.projectsc;

import de.projectsc.core.Core;
import de.projectsc.gui.GUICore;

public class Main {

    public static void main(String[] args) {
        Core core = new Core();
        final GUICore gui = new GUICore(core.getGuiIncomingQueue(), core.getGuiOutgoingQueue());
        new Thread(core).start();
        new Thread(gui).start();
    }
}
