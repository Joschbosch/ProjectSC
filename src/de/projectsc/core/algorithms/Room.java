package de.projectsc.core.algorithms;

public class Room {
	int[][] roomTiles;

	public Room(int width, int height) {
		roomTiles = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				roomTiles[i][j] = 1;
			}
		}
	}

	public boolean isFree(int x, int y) {
		return roomTiles[x][y] == 1;
	}

	public boolean mustBeSolid(int x, int y) {
		return roomTiles[x][y] == 0;
	}
}
