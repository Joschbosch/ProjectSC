package de.projectsc.core.data;

public class Map {
	private final int mapWidth;
	private final int mapHeight;
	private Tile[][] map;

	final Tile nothing = new Tile(TileType.NOTHING);

	final Tile gras = new Tile(TileType.GRAS);

	public Map(int width, int height) {
		mapWidth = width;
		mapHeight = height;

		initMap();
	}

	private void initMap() {

		map = new Tile[mapWidth][mapHeight];
		for (int i = 0; i < mapWidth; i++) {
			for (int j = 0; j < mapHeight; j++) {
				map[i][j] = nothing;
			}
		}
	}

	public void printMap() {
		for (int i = 0; i < mapWidth; i++) {
			for (int j = 0; j < mapHeight; j++) {
				System.out.print(map[i][j].type.getRepresentation());
			}
			System.out.println();
		}
	}

	public Tile getTileAt(int x, int y) {
		return map[x][y];
	}

	public int getWidth() {
		return mapWidth;
	}

	public int getHeight() {
		return mapHeight;
	}

	public void setTileAt(int i, int j, TileType type) {
		if (type == TileType.GRAS) {
			map[i][j] = gras;
		} else if (type == TileType.NOTHING) {
			map[i][j] = nothing;
		}
	}

	public boolean inBounds(int x, int y) {
		if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
			return true;
		}
		return false;
	}
}
