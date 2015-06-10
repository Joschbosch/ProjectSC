package de.projectsc;

import de.projectsc.core.Core;
import de.projectsc.core.algorithms.MapGenerator;
import de.projectsc.core.data.Map;

public class Main {

    public static void main(String[] args) {
        // m.printMap();
        final Core core = new Core();
        new Thread(new Runnable() {

            @Override
            public void run() {
                core.start();
            }
        }).start();

        while (!core.isRunning()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 100 && core.isRunning(); i++) {
                    Map m = new Map(100, 100);
                    MapGenerator.createRandomMap(i, m);
                    core.setMap(m);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }
}
