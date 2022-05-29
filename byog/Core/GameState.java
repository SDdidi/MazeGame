package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.Random;

public class GameState implements Serializable {
    TETile[][][] allWorld;
    Position player;
    int stepsLeft;
    int floorNums;
    int currentFloor;
    boolean hasKey;
    Position[] upstairsPosition;
    Position[] downstairsPosition;
    Random RANDOM;

    public GameState() {
        stepsLeft = 200;
        floorNums = 3;
        currentFloor = 0;
        hasKey = false;
        player = new Position();
        allWorld = new TETile[floorNums][][];
        upstairsPosition = new Position[floorNums];
        downstairsPosition = new Position[floorNums];
        RANDOM = new Random();
    }

}
