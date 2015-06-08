package de.projectsc.core.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.projectsc.core.data.Map;
import de.projectsc.core.data.TileType;

public class MapGenerator {
    private static final int MAX_RETRIES_FOR_ROOMS = 150;
    private static final int MAX_ROOM_SIZE = 10;
    private static final int MIN_ROOM_SIZE = 2;
    private static Random r;

    public static Map createRandomMap(int seed, Map map) {

        r = new Random(seed);

        createRooms(map);
        createPerfectMaze(map);
        for (int i = 0; i < 20; i++) {
            fillBackIn(map);
        }
        connect(map);
        return map;
    }

    private static void connect(Map map) {
        for (int i = 1; i < map.getWidth() - 1; i++) {
            for (int j = 1; j < map.getHeight() - 1; j++) {
                if (map.getTileAt(i, j).getType() == TileType.NOTHING
                        && (map.getTileAt(i + 1, j).getType() == TileType.GRAS && map
                                .getTileAt(i - 1, j).getType() == TileType.GRAS)
                        || (map.getTileAt(i, j + 1).getType() == TileType.GRAS && map
                                .getTileAt(i, j - 1).getType() == TileType.GRAS)) {
                    map.setTileAt(i, j, TileType.GRAS);

                }
            }
        }
    }

    private static void fillBackIn(Map map) {
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.getTileAt(i, j).getType() == TileType.GRAS
                        && surroundedByThreeOrFour(i, j, map)) {
                    map.setTileAt(i, j, TileType.NOTHING);
                }
            }
        }
    }

    private static boolean surroundedByThreeOrFour(int i, int j, Map map) {
        int count = 0;
        if (map.inBounds(i + 1, j)) {
            if (map.getTileAt(i + 1, j).getType() == TileType.NOTHING) {
                count++;
            }
        } else {
            count++;
        }
        if (map.inBounds(i, j + 1)) {
            if (map.getTileAt(i, j + 1).getType() == TileType.NOTHING) {
                count++;
            }
        } else {
            count++;
        }
        if (map.inBounds(i - 1, j)) {
            if (map.getTileAt(i - 1, j).getType() == TileType.NOTHING) {
                count++;
            }
        } else {
            count++;
        }
        if (map.inBounds(i, j - 1)) {
            if (map.getTileAt(i, j - 1).getType() == TileType.NOTHING) {
                count++;
            }
        } else {
            count++;
        }
        return count >= 3;
    }

    private static void createPerfectMaze(Map map) {
        for (int i = 1; i < map.getWidth() - 1; i++) {
            for (int j = 1; j < map.getHeight() - 1; j++) {
                if (map.getTileAt(i, j).getType() == TileType.NOTHING
                        && surroundedNothing(i, j, map)) {
                    startMaze(i, j, map);
                }
            }
        }
    }

    private static void startMaze(int i, int j, Map map) {
        map.setTileAt(i, j, TileType.GRAS);
        List<Integer[]> directions = new LinkedList<Integer[]>();
        directions.add(new Integer[] { 1, 0 });
        directions.add(new Integer[] { -1, 0 });
        directions.add(new Integer[] { 0, 1 });
        directions.add(new Integer[] { 0, -1 });

        while (!directions.isEmpty()) {
            Integer[] direction = directions.get(r.nextInt(directions.size()));
            if (map.inBounds(i + direction[0], j + direction[1])
                    && map.getTileAt(i + direction[0], j + direction[1])
                            .getType() == TileType.NOTHING
                    && surroundedNothing(i + direction[0], j + direction[1], i,
                            j, map)) {
                startMaze(i + direction[0], j + direction[1], map);
            }
            directions.remove(direction);
        }
    }

    private static boolean surroundedNothing(int i, int j, int exceptX,
            int exceptY, Map map) {
        boolean nothing = map.inBounds(i, j);

        if (nothing) {
            if (i + 1 < map.getWidth() && i + 1 != exceptX
                    && map.getTileAt(i + 1, j).getType() != TileType.NOTHING) {
                nothing = false;
            }
            if (j + 1 < map.getHeight() && j + 1 != exceptY
                    && map.getTileAt(i, j + 1).getType() != TileType.NOTHING) {
                nothing = false;
            }
            if (i - 1 >= 0 && i - 1 != exceptX
                    && map.getTileAt(i - 1, j).getType() != TileType.NOTHING) {
                nothing = false;
            }
            if (j - 1 >= 0 && j - 1 != exceptY
                    && map.getTileAt(i, j - 1).getType() != TileType.NOTHING) {
                nothing = false;
            }
        }
        return nothing;
    }

    private static boolean surroundedNothing(int i, int j, Map map) {
        boolean nothing = map.inBounds(i, j);
        if (nothing) {
            if (map.getTileAt(i + 1, j).getType() != TileType.NOTHING) {
                nothing = false;
            }
            if (map.getTileAt(i, j + 1).getType() != TileType.NOTHING) {
                nothing = false;
            }
            if (map.getTileAt(i - 1, j).getType() != TileType.NOTHING) {
                nothing = false;
            }
            if (map.getTileAt(i, j - 1).getType() != TileType.NOTHING) {
                nothing = false;
            }
        }
        return nothing;
    }

    private static void createRooms(Map map) {
        for (int attempts = 0; attempts < MAX_RETRIES_FOR_ROOMS; attempts++) {
            int roomWidth = (r.nextInt(MAX_ROOM_SIZE) + MIN_ROOM_SIZE) * 2 + 1;
            int roomHeight = (r.nextInt(MAX_ROOM_SIZE) + MIN_ROOM_SIZE) * 2 + 1;
            Room room = new Room(roomWidth, roomHeight);

            int roomPositionX = 2 * r.nextInt(map.getWidth() + 1);
            int roomPositionY = 2 * r.nextInt(map.getHeight() + 1);
            if (checkIfRoomFits(room, roomPositionX, roomPositionY, map)) {
                applyRoom(room, roomPositionX, roomPositionY, map);
            }
        }
    }

    private static void applyRoom(Room room, int roomPositionX,
            int roomPositionY, Map map) {
        for (int i = 0; i < room.roomTiles.length; i++) {
            for (int j = 0; j < room.roomTiles[i].length; j++) {
                if (room.isFree(i, j)) {
                    map.setTileAt(i + roomPositionX, j + roomPositionY,
                            TileType.GRAS);
                }
            }
        }
    }

    private static boolean checkIfRoomFits(Room room, int roomPositionX,
            int roomPositionY, Map map) {
        boolean isFree = true;

        if (roomPositionX + room.roomTiles.length >= map.getWidth()
                || roomPositionY + room.roomTiles[0].length >= map.getHeight()) {
            isFree = false;
        }
        if (isFree) {
            for (int i = 0; i < room.roomTiles.length; i++) {
                for (int j = 0; j < room.roomTiles[i].length; j++) {
                    if ((room.isFree(i, j) && map.getTileAt(i + roomPositionX,
                            j + roomPositionY).getType() != TileType.NOTHING)) {
                        isFree = false;
                    }
                }
            }
        }
        return isFree;
    }
}
