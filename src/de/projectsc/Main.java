package de.projectsc;

import de.projectsc.core.algorithms.MapGenerator;
import de.projectsc.core.data.Map;

public class Main {

	public static void main(String[] args) {
		// new Core().start();
		Map m = new Map(100, 100);
		MapGenerator.createRandomMap(0, m);
		m.printMap();
	}

}
