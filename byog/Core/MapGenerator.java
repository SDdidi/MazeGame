package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    public static final int deltamin = 3;
    public static final int deltamax = 10;
    public static final int numsSupremum = 60;
    public static final int numsInfimum = 30;
    public final int WIDTH;
    public final int HEIGHT;
    public final Random RANDOM;
    private TETile[][] world;
    public List<Room> roomList;


    public MapGenerator(int w, int h, Random r) {
        WIDTH = w;
        HEIGHT = h;
        RANDOM = r;
        world = new TETile[WIDTH][HEIGHT];
        roomList = new LinkedList<Room>();
    }

    private void initialize() {
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }


    private boolean checkOverlap(Room otherRoom) {
        if (roomList.isEmpty()) {
            return false;
        }
        for (Room r : roomList) {
            if (r.overlap(otherRoom)) {
                return true;
            }
        }
        return false;
    }

    public TETile[][] getWorld() {
        return world;
    }


    private void createOneRoom(Breakpoint pp) {
        int roomWidth = RandomUtils.uniform(RANDOM, deltamin, deltamax);
        int roomHeight = RandomUtils.uniform(RANDOM, deltamin, deltamax);
        int count = 1;
        while (helperCreateOneRoom(pp, roomWidth, roomHeight) == false && count < (deltamax - deltamin) * (deltamax - deltamin) + 1) {
            roomWidth = RandomUtils.uniform(RANDOM, deltamin, deltamax);
            roomHeight = RandomUtils.uniform(RANDOM, deltamin, deltamax);
            count += 1;
        }
    }

    private boolean helperCreateOneRoom(Breakpoint pp, int roomWidth, int roomHeight) {
        boolean returnVal = false;
        if (pp.direction[0] == 1 && pp.direction[1] == 0) {
            Position p2 = new Position(0, 0);
            p2.x = pp.breakP.x;
            int ry = RandomUtils.uniform(RANDOM, roomHeight - 1);
            p2.y = pp.breakP.y + ry + 1;
            Room newRoom = new Room(pp, roomWidth, roomHeight, p2);
            int count = 1;
            while (checkRoom(newRoom) == false || checkOverlap(newRoom) == true) {
                if (count >= roomHeight) {
                    break;
                }
                ry = RandomUtils.uniform(RANDOM, roomHeight - 1);
                p2.y = pp.breakP.y + ry + 1;
                newRoom = new Room(pp, roomWidth, roomHeight, p2);
                count += 1;
            }
            if (count < roomHeight) {
                roomList.add(newRoom);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }

        }
        if (pp.direction[0] == -1 && pp.direction[1] == 0) {
            Position p1 = new Position(0, 0);
            p1.x = pp.breakP.x;
            int ry = RandomUtils.uniform(RANDOM, roomHeight - 1);
            p1.y = pp.breakP.y - ry - 1;
            Room newRoom = new Room(pp, p1, roomWidth, roomHeight);
            int count = 1;
            while (checkRoom(newRoom) == false || checkOverlap(newRoom) == true) {
                if (count >= roomHeight) {
                    break;
                }
                ry = RandomUtils.uniform(RANDOM, roomHeight - 1);
                p1.y = pp.breakP.y - ry - 1;
                newRoom = new Room(pp, p1, roomWidth, roomHeight);
                count += 1;
            }
            if (count < roomHeight) {
                roomList.add(newRoom);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        if (pp.direction[1] == 1 && pp.direction[0] == 0) {
            Position p2 = new Position(0, 0);
            p2.y = pp.breakP.y;
            int rx = RandomUtils.uniform(RANDOM, roomWidth - 1);
            p2.x = pp.breakP.x + rx + 1;
            Room newRoom = new Room(pp, roomWidth, roomHeight, p2);
            int count = 1;
            while (checkRoom(newRoom) == false || checkOverlap(newRoom) == true) {
                if (count >= roomWidth) {
                    break;
                }
                rx = RandomUtils.uniform(RANDOM, roomWidth - 1);
                p2.x = pp.breakP.x + rx + 1;
                newRoom = new Room(pp, roomWidth, roomHeight, p2);
                count += 1;
            }
            if (count < roomWidth) {
                roomList.add(newRoom);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        if (pp.direction[1] == -1 && pp.direction[0] == 0) {
            Position p1 = new Position(0, 0);
            p1.y = pp.breakP.y;
            int rx = RandomUtils.uniform(RANDOM, roomWidth - 1);
            p1.x = pp.breakP.x - rx - 1;
            Room newRoom = new Room(pp, p1, roomWidth, roomHeight);
            int count = 1;
            while (checkRoom(newRoom) == false || checkOverlap(newRoom) == true) {
                if (count >= roomWidth) {
                    break;
                }
                rx = RandomUtils.uniform(RANDOM, roomWidth - 1);
                p1.x = pp.breakP.x - rx - 1;
                newRoom = new Room(pp, p1, roomWidth, roomHeight);
                count += 1;
            }
            if (count < roomWidth) {
                roomList.add(newRoom);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        return returnVal;
    }

    private boolean checkPosition(Position p) {
        if (p.x >= 0 && p.x < WIDTH && p.y >= 0 && p.y < HEIGHT) {
            return true;
        }
        return false;
    }

    private boolean checkRoom(Room rm) {
        if (checkPosition(rm.p1) && checkPosition(rm.p2)) {
            return true;
        }
        return false;
    }

    private void drawMap() {
        for (Room rm : roomList) {
            drawRoom(rm);
        }

    }

    private void drawRoom(Room rm) {
        for (int i = rm.p1.x; i <= rm.p2.x; i += 1) {
            world[i][rm.p1.y] = TETile.colorVariant(Tileset.WALL, 0, 0, 100, RANDOM);
            world[i][rm.p2.y] = TETile.colorVariant(Tileset.WALL, 0, 0, 100, RANDOM);
        }
        for (int j = rm.p1.y; j <= rm.p2.y; j += 1) {
            world[rm.p1.x][j] = TETile.colorVariant(Tileset.WALL, 0, 0, 100, RANDOM);
            world[rm.p2.x][j] = TETile.colorVariant(Tileset.WALL, 0, 0, 100, RANDOM);
        }
        for (int i = rm.p1.x + 1; i < rm.p2.x; i += 1) {
            for (int j = rm.p1.y + 1; j < rm.p2.y; j += 1) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        for (Breakpoint pp : rm.BPS) {
            world[pp.breakP.x][pp.breakP.y] = Tileset.FLOOR;
        }
    }

    // Hallways are special rooms. Width od Height of the room should be 2.
    private void createOneHallway(Breakpoint pp) {
        int roomWidth = RandomUtils.uniform(RANDOM, deltamin, deltamax);
        int roomHeight = RandomUtils.uniform(RANDOM, deltamin, deltamax);
        int count = 1;
        while (helperCreateOneHallway(pp, roomWidth, roomHeight) == false && count < deltamax - deltamin + 1) {
            roomWidth = RandomUtils.uniform(RANDOM, deltamin, deltamax);
            roomHeight = RandomUtils.uniform(RANDOM, deltamin, deltamax);
            count += 1;
        }
    }

    private boolean helperCreateOneHallway(Breakpoint pp, int roomWidth, int roomHeight) {
        boolean returnVal = false;

        if (pp.direction[0] == 1 && pp.direction[1] == 0) {
            Position p2 = new Position(0, 0);
            p2.x = pp.breakP.x;
            p2.y = pp.breakP.y + 1;
            Room newHallway = new Room(pp, roomWidth, 2, p2);
            int count = 1;
            while (checkRoom(newHallway) == false || checkOverlap(newHallway) == true) {
                if (count > deltamax - 3) {
                    break;
                }
                roomWidth = RandomUtils.uniform(RANDOM, 3, deltamax);
                newHallway = new Room(pp, roomWidth, 2, p2);
                count += 1;
            }
            if (count <= deltamax - 3) {
                roomList.add(newHallway);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        if (pp.direction[0] == -1 && pp.direction[1] == 0) {
            Position p1 = new Position(0, 0);
            p1.x = pp.breakP.x;
            p1.y = pp.breakP.y - 1;
            Room newHallway = new Room(pp, p1, roomWidth, 2);
            int count = 1;
            while (checkRoom(newHallway) == false || checkOverlap(newHallway) == true) {
                if (count > deltamax - 3) {
                    break;
                }
                roomWidth = RandomUtils.uniform(RANDOM, 3, deltamax);
                newHallway = new Room(pp, p1, roomWidth, 2);
                count += 1;
            }
            if (count <= deltamax - 3) {
                roomList.add(newHallway);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        if (pp.direction[1] == 1 && pp.direction[0] == 0) {
            Position p2 = new Position(0, 0);
            p2.x = pp.breakP.x + 1;
            p2.y = pp.breakP.y;
            Room newHallway = new Room(pp, 2, roomHeight, p2);
            int count = 1;
            while (checkRoom(newHallway) == false || checkOverlap(newHallway) == true) {
                if (count > deltamax - 3) {
                    break;
                }
                roomHeight = RandomUtils.uniform(RANDOM, 3, deltamax);
                newHallway = new Room(pp, 2, roomHeight, p2);
                count += 1;
            }
            if (count <= deltamax - 3) {
                roomList.add(newHallway);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        if (pp.direction[1] == -1 && pp.direction[0] == 0) {
            Position p1 = new Position(0, 0);
            p1.x = pp.breakP.x - 1;
            p1.y = pp.breakP.y;
            Room newHallway = new Room(pp, p1, 2, roomHeight);
            int count = 1;
            while (checkRoom(newHallway) == false || checkOverlap(newHallway) == true) {
                if (count > deltamax - 3) {
                    break;
                }
                roomHeight = RandomUtils.uniform(RANDOM, 3, deltamax);
                newHallway = new Room(pp, p1, 2, roomHeight);
                count += 1;
            }
            if (count <= deltamax - 3) {
                roomList.add(newHallway);
                pp.roomIndex = roomList.size() - 1;
                returnVal = true;
            }
        }
        return returnVal;
    }

    private int[] randomDirection(Room rm) {
        if (rm.p2.x - rm.p1.x == 2) {
            if (RandomUtils.uniform(RANDOM, 2) == 0) {
                return new int[]{1, 0};
            } else {
                return new int[]{-1, 0};
            }
        }
        if (rm.p2.y - rm.p1.y == 2) {
            if (RandomUtils.uniform(RANDOM, 2) == 0) {
                return new int[]{0, 1};
            } else {
                return new int[]{0, -1};
            }
        }
        switch (RandomUtils.uniform(RANDOM, 4)) {
            case 0:
                return new int[]{1, 0};
            case 1:
                return new int[]{-1, 0};
            case 2:
                return new int[]{0, 1};
            case 3:
                return new int[]{0, -1};
            default:
                return new int[]{0, -1};
        }
    }

    private int[] getUnusedDirection(Room rm) {
        int[] newDirection = randomDirection(rm);
        while (checkDirection(rm, newDirection) == false) {
            newDirection = randomDirection(rm);
        }
        return newDirection;
    }

    private boolean checkDirection(Room rm, int[] direction) {
        for (Breakpoint bp : rm.BPS) {
            if (Arrays.equals(direction, bp.direction)) {
                return false;
            }
        }
        if (rm.p2.x - rm.p1.x == 2 || rm.p2.y - rm.p1.y == 2) {
            if (direction[0] + rm.BPS.get(0).direction[0] == 0
                    && direction[1] + rm.BPS.get(0).direction[1] == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnusedDirection(Room rm) {
        if (rm.p2.x - rm.p1.x == 2 || rm.p2.y - rm.p1.y == 2) {
            return rm.BPS.size() < 3;
        }
        return rm.BPS.size() < 4;
    }

    private Breakpoint getNewBreakpoint(Room rm, int[] direction) {
        Position p = new Position(0, 0);
        int roomWidth = rm.p2.x - rm.p1.x;
        int roomHeight = rm.p2.y - rm.p1.y;
        if (direction[0] == 1 && direction[1] == 0) {
            p.x = rm.p2.x;
            p.y = rm.p1.y + 1 + RandomUtils.uniform(RANDOM, roomHeight - 1);
        }
        if (direction[0] == -1 && direction[1] == 0) {
            p.x = rm.p1.x;
            p.y = rm.p1.y + 1 + RandomUtils.uniform(RANDOM, roomHeight - 1);
        }
        if (direction[1] == 1 && direction[0] == 0) {
            p.y = rm.p2.y;
            p.x = rm.p1.x + 1 + RandomUtils.uniform(RANDOM, roomWidth - 1);
        }
        if (direction[1] == -1 && direction[0] == 0) {
            p.y = rm.p1.y;
            p.x = rm.p1.x + 1 + RandomUtils.uniform(RANDOM, roomWidth - 1);
        }
        return new Breakpoint(p, direction);
    }

    private Breakpoint findNextBreakpoint() {
        int whichRoom = RandomUtils.uniform(RANDOM, roomList.size());
        Room newRoom = roomList.get(whichRoom);
        while (hasUnusedDirection(newRoom) == false) {
            whichRoom = RandomUtils.uniform(RANDOM, roomList.size());
            newRoom = roomList.get(whichRoom);
        }
        int[] newDirection = getUnusedDirection(newRoom);
        Breakpoint newBP = getNewBreakpoint(newRoom, newDirection);
        if (checkBreakpoint(newBP) == true) {
            newBP.roomIndex = whichRoom;
            newRoom.BPS.add(newBP);
            return newBP;
        }
        return findNextBreakpoint();
    }

    private boolean checkBreakpoint(Breakpoint pp) {
        if (roomList.size() == 0) {
            return true;
        }
        pp = pp.getTwin();
        for (Room rm : roomList) {
            if (rm.contain(pp.breakP) == true) {
                return false;
            }
        }
        if (pp.breakP.x < deltamin + 1 || WIDTH - pp.breakP.x < deltamin + 2
                || pp.breakP.y < deltamin + 1 || HEIGHT - pp.breakP.y < deltamin + 2) {
            return false;
        }
        return true;
    }

    private void removeBreakpoint(Breakpoint bp) {
        int index = bp.roomIndex;
        Room newRoom = new Room(roomList.get(index));
        newRoom.BPS.remove(bp);
        roomList.set(index, newRoom);
    }


    public void generate() {
        initialize();
        Position fp = new Position(0, 0);
        fp.x = (int) RandomUtils.gaussian(RANDOM, WIDTH / 2.0, WIDTH / 8.0);
        fp.y = (int) RandomUtils.gaussian(RANDOM, HEIGHT / 2.0, HEIGHT / 8.0);
        Breakpoint bp1 = new Breakpoint((fp), new int[]{-1, 0});
        createOneRoom(bp1);
        bp1.roomIndex = 0;
        Breakpoint bp2 = bp1.getTwin();
        int count = 0;
        int nums = RandomUtils.uniform(RANDOM, numsInfimum, numsSupremum);
        while (roomList.size() < nums && count < 100000) {
            if (RandomUtils.bernoulli(RANDOM) == true) {
                createOneRoom(bp2);
            } else {
                createOneHallway(bp2);
            }
            if (bp2.roomIndex == -1) {
                removeBreakpoint(bp1);
            }
            bp1 = findNextBreakpoint();
            bp2 = bp1.getTwin();
            count += 1;
        }
        removeBreakpoint(bp1);
        drawMap();
    }
}

class Position implements Serializable {
    int x;
    int y;

    Position() {
        x = 0;
        y = 0;
    }

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Position(Position p) {
        x = p.x;
        y = p.y;
    }

    boolean equals(Position other) {
        if (x == other.x && y == other.y) {
            return true;
        }
        return false;
    }

}
