package de.projectsc.core.data;

public enum TileType {
	NOTHING(0), GRAS(1), WATER(2), ROCK(3);

	private final int representation;

	TileType(int representation) {
		this.representation = representation;
	}

	public int getRepresentation() {
		return representation;
	}
}
