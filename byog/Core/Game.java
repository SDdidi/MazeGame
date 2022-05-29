package byog.Core;

import byog.TileEngine.StdDraw;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.awt.*;
import java.io.*;
import java.util.Random;

public class Game implements Serializable {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 45;
    public static final int bottom = 5;
    public static final int maxWater = 20;
    public static final int waterSupply = 20;
    public static final int startSteps = 200;
    public static final int maxFloor = 5;
    public static final Font largeFont = new Font("Large", Font.BOLD, 50);
    public static final Font middleFont = new Font("Middle", Font.BOLD, 30);
    public static final Font smallFont = new Font("Small", Font.BOLD, 20);
    public static final Font font = new Font("Monaco", Font.BOLD, 14);
    public static final TERenderer ter = new TERenderer();
    public static final String file = "./Old Game.ser";


    GameState gs;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(largeFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2.0, HEIGHT * 3 / 4.0, "CS61B: THE GAME");
        StdDraw.setFont(middleFont);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 3, "Load Game(L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 6, "Quit(Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();


        char c = 0;
        while (c == 0) {
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
            }
        }

        if (c == 'n' || c == 'N') {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(middleFont);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Please enter a seed(end with S):");
            StdDraw.show();
            StringBuilder s = new StringBuilder();
            char cc = 0;
            while (cc != 'S' && cc != 's') {
                if (StdDraw.hasNextKeyTyped()) {
                    cc = StdDraw.nextKeyTyped();
                    s.append(cc);
                    StdDraw.textLeft(WIDTH / 2.0, HEIGHT / 2.0 - 4.0, s.toString());
                    StdDraw.show();
                }
            }
            StdDraw.clear(Color.BLACK);
            long seed = Long.parseLong(s.toString().substring(0, s.toString().length() - 1));
            gs = new GameState();
            gs.RANDOM = new Random(seed);
            ter.initialize(WIDTH, HEIGHT, 0, bottom);
            startNewGame(gs.RANDOM);
            showOnScreen(0, 0);
            showMouseInfo();
            playGameWithKeyboard();
        }
        if (c == 'q' || c == 'Q') {
            System.exit(0);
        }
        if (c == 'l' || c == 'L') {
            gs = readGame();
            if (gs == null) {
                gs = new GameState();
                ter.initialize(WIDTH, HEIGHT, 0, bottom);
                startNewGame(gs.RANDOM);
            } else {
                ter.initialize(WIDTH, HEIGHT, 0, bottom);
            }
            showOnScreen(0, 0);
            showMouseInfo();
            playGameWithKeyboard();
        }

    }


    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        TETile[][] finalWorldFrame = null;
        String firstPart = input.split(":")[0];
        String lastPart = "";
        if (input.split(":").length > 1) {
            lastPart = input.split(":")[1];
        }
        String middlePart;
        if (firstPart.charAt(0) == 'L' || firstPart.charAt(0) == 'l') {
            finalWorldFrame = playGameWithString(firstPart);
        } else {
            if (firstPart.split("(?i)s", 2).length == 1) {
                middlePart = "";
                firstPart = firstPart.split("(?i)s", 2)[0];
            } else {
                middlePart = firstPart.split("(?i)s", 2)[1];
                firstPart = firstPart.split("(?i)s", 2)[0];
            }
            long seed;
            if (firstPart.length() == 1) {
                seed = 0;
            } else {
                seed = Long.parseLong(firstPart.substring(1));
            }
            gs = new GameState();
            gs.RANDOM = new Random(seed);
            startNewGame(gs.RANDOM);
            finalWorldFrame = playGameWithString(middlePart);
        }
        if (lastPart.length() > 0) {
            if (lastPart.charAt(0) == 'q' || lastPart.charAt(0) == 'Q') {
                writeGame(gs);
            }
        }
        return finalWorldFrame;
    }

    public void startNewGame(Random RANDOM) {
        gs.floorNums = RandomUtils.uniform(RANDOM, 2, maxFloor);
        gs.allWorld = new TETile[gs.floorNums][][];
        gs.upstairsPosition = new Position[gs.floorNums];
        gs.downstairsPosition = new Position[gs.floorNums];
        gs.stepsLeft = startSteps;
        gs.hasKey = false;

        MapGenerator mg = new MapGenerator(WIDTH, HEIGHT - bottom, RANDOM);
        for (int i = 0; i < gs.floorNums; i += 1) {
            mg.generate();
            gs.allWorld[i] = TETile.copyOf(mg.getWorld());
            int water = RandomUtils.uniform(RANDOM, maxWater / 2, maxWater);
            while (water > 0) {
                randomSet(RANDOM, Tileset.WATER, i);
                water -= 1;
            }
        }

        gs.player = randomSet(RANDOM, Tileset.PLAYER, 0);
        gs.currentFloor = 0;
        randomSet(RANDOM, Tileset.KEY, RandomUtils.uniform(RANDOM, gs.floorNums));
        randomSetGoldenDoor(RANDOM, mg.WIDTH, mg.HEIGHT, RandomUtils.uniform(RANDOM, gs.floorNums));
        for (int i = 0; i < gs.floorNums - 1; i += 1) {
            gs.upstairsPosition[i] = randomSetStairs(RANDOM, Tileset.UPSTAIRS, i);
        }
        gs.upstairsPosition[gs.floorNums - 1] = new Position(0, 0);
        for (int i = 1; i < gs.floorNums; i += 1) {
            gs.downstairsPosition[i] = randomSetStairs(RANDOM, Tileset.DOWNSTAIRS, i);
        }
        gs.downstairsPosition[0] = new Position(0, 0);

    }

    private void showOnScreen(int floor, int stateID) {
        StdDraw.setFont(font);
        ter.renderFrame(gs.allWorld[floor]);
        playerState(stateID);
    }

    private void randomSetGoldenDoor(Random RANDOM, int w, int h, int floor) {
        int x = RandomUtils.uniform(RANDOM, 1, w - 1);
        int y = RandomUtils.uniform(RANDOM, 1, h - 1);
        if (gs.allWorld[floor][x][y].equals(Tileset.WALL)) {
            if ((gs.allWorld[floor][x - 1][y].equals(Tileset.NOTHING) && gs.allWorld[floor][x + 1][y].equals(Tileset.FLOOR))
                    || (gs.allWorld[floor][x + 1][y].equals(Tileset.NOTHING) && gs.allWorld[floor][x - 1][y].equals(Tileset.FLOOR))
                    || (gs.allWorld[floor][x][y - 1].equals(Tileset.NOTHING) && gs.allWorld[floor][x][y + 1].equals(Tileset.FLOOR))
                    || (gs.allWorld[floor][x][y + 1].equals(Tileset.NOTHING) && gs.allWorld[floor][x][y - 1].equals(Tileset.FLOOR))) {
                gs.allWorld[floor][x][y] = Tileset.LOCKED_DOOR;
                return;
            }
        }
        randomSetGoldenDoor(RANDOM, w, h, floor);
    }

    private void playerState(int n) {

        String[] information = new String[]{"", "Drank water! Steps + " + waterSupply, "Got the key!", "Need a key."};
        StdDraw.line(0.0, bottom - 1, WIDTH - 1.0, bottom - 1);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2.0, bottom / 2.0, "Steps Left:" + gs.stepsLeft + " || Floor:" + (gs.currentFloor + 1) + " (" + gs.floorNums
                + " Floors in total)");
        StdDraw.textLeft(5.0, bottom / 2.0, information[n]);
        StdDraw.show();
    }

    private Position randomSet(Random RANDOM, TETile t, int floor) {
        int x = RandomUtils.uniform(RANDOM, WIDTH);
        int y = RandomUtils.uniform(RANDOM, HEIGHT - bottom);
        while (!gs.allWorld[floor][x][y].equals(Tileset.FLOOR)) {
            x = RandomUtils.uniform(RANDOM, WIDTH);
            y = RandomUtils.uniform(RANDOM, HEIGHT - bottom);
        }
        gs.allWorld[floor][x][y] = t;
        return new Position(x, y);
    }

    private Position randomSetStairs(Random RANDOM, TETile stairs, int floor) {
        int x = RandomUtils.uniform(RANDOM, 1, WIDTH - 1);
        int y = RandomUtils.uniform(RANDOM, 1, HEIGHT - bottom - 1);
        boolean check = true;
        for (int i = -1; i < 2; i += 1) {
            for (int j = -1; j < 2; j += 1) {
                if (!gs.allWorld[floor][x + i][y + j].equals(Tileset.FLOOR)) {
                    check = false;
                }
            }
        }
        if (check) {
            gs.allWorld[floor][x][y] = stairs;
            return new Position(x, y);
        } else {
            return randomSetStairs(RANDOM, stairs, floor);
        }
    }

    //returnVal[0] = {1:game over, 0: game not over},returnVal[1] = {0,1,2,3} for playerState.
    public int[] processChar(char c) {
        if (c == 'a' || c == 'A') {
            return move(new Position(gs.player.x - 1, gs.player.y));
        }
        if (c == 'w' || c == 'W') {
            return move(new Position(gs.player.x, gs.player.y + 1));
        }
        if (c == 'd' || c == 'D') {
            return move(new Position(gs.player.x + 1, gs.player.y));
        }
        if (c == 's' || c == 'S') {
            return move(new Position(gs.player.x, gs.player.y - 1));
        }
        if (c == 'q' || c == 'Q') {
            writeGame(gs);
            System.exit(0);
        }
        if (c == 'L' || c == 'l') {
            gs = readGame();
        }
        return new int[2];
    }

    private void helperMove(Position next) {
        gs.allWorld[gs.currentFloor][gs.player.x][gs.player.y] = Tileset.FLOOR;
        gs.player = next;
        gs.allWorld[gs.currentFloor][gs.player.x][gs.player.y] = Tileset.PLAYER;
        gs.stepsLeft -= 1;
    }

    //returnVal the same as processChar.
    private int[] move(Position next) {
        int[] returnVal = new int[2];
        if (gs.allWorld[gs.currentFloor][next.x][next.y].equals(Tileset.FLOOR)) {
            helperMove(next);
            returnVal[0] = 0;
            returnVal[1] = 0;
        }
        if (gs.allWorld[gs.currentFloor][next.x][next.y].equals(Tileset.WATER)) {
            helperMove(next);
            gs.stepsLeft += waterSupply;
            returnVal[0] = 0;
            returnVal[1] = 1;
        }
        if (gs.allWorld[gs.currentFloor][next.x][next.y].equals(Tileset.KEY)) {
            helperMove(next);
            gs.hasKey = true;
            returnVal[0] = 0;
            returnVal[1] = 2;
        }
        if (gs.allWorld[gs.currentFloor][next.x][next.y].equals(Tileset.LOCKED_DOOR)) {
            if (gs.hasKey == false) {
                returnVal[0] = 0;
                returnVal[1] = 3;
            } else {
                gameOver(true);
                returnVal[0] = 1;
            }
        }
        if (gs.allWorld[gs.currentFloor][next.x][next.y].equals(Tileset.UPSTAIRS)) {
            gs.allWorld[gs.currentFloor][gs.player.x][gs.player.y] = Tileset.FLOOR;
            gs.stepsLeft -= 1;
            gs.currentFloor += 1;
            returnVal[0] = 0;
            returnVal[1] = besideStairs("downstairs");
        }
        if (gs.allWorld[gs.currentFloor][next.x][next.y].equals(Tileset.DOWNSTAIRS)) {
            gs.allWorld[gs.currentFloor][gs.player.x][gs.player.y] = Tileset.FLOOR;
            gs.stepsLeft -= 1;
            gs.currentFloor -= 1;
            returnVal[0] = 0;
            returnVal[1] = besideStairs("upstairs");
        }
        return returnVal;
    }

    private void playerBesidesStairs(int x, int y) {
        gs.player.x = x;
        gs.player.y = y;
        gs.allWorld[gs.currentFloor][x][y] = Tileset.PLAYER;
    }

    //returnVal[0]={1:canStayAt (x,y), 0:cannot stay at there}, returnVal[1] playerState
    private int[] canStayAt(int x, int y) {
        int[] returnVal = new int[2];
        if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.FLOOR)) {
            playerBesidesStairs(x, y);
            returnVal[0] = 1;
            returnVal[1] = 0;
        }
        if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.WATER)) {
            playerBesidesStairs(x, y);
            gs.stepsLeft += waterSupply;
            returnVal[0] = 1;
            returnVal[1] = 1;
        }
        if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.KEY)) {
            playerBesidesStairs(x, y);
            gs.hasKey = true;
            returnVal[0] = 1;
            returnVal[1] = 2;
        }
        return returnVal;
    }

    //returnVal playerState
    private int besideStairs(String which) {
        int x;
        int y;

        if (which.equals("upstairs")) {
            x = gs.upstairsPosition[gs.currentFloor].x;
            y = gs.upstairsPosition[gs.currentFloor].y;
        } else {
            x = gs.downstairsPosition[gs.currentFloor].x;
            y = gs.downstairsPosition[gs.currentFloor].y;
        }

        if (y + 1 < gs.allWorld[gs.currentFloor][0].length) {
            int[] val = canStayAt(x, y + 1);
            if (val[0] == 1) {
                return val[1];
            }
        }
        if (y - 1 >= 0) {
            int[] val = canStayAt(x, y - 1);
            if (val[0] == 1) {
                return val[1];
            }
        }
        if (x + 1 < gs.allWorld[gs.currentFloor].length) {
            int[] val = canStayAt(x + 1, y);
            if (val[0] == 1) {
                return val[1];
            }
        }
        if (x - 1 >= 0) {
            int[] val = canStayAt(x - 1, y);
            if (val[0] == 1) {
                return val[1];
            }
        }
        return 0;
    }


    private void playGameWithKeyboard() {
        while (gs.stepsLeft > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                int[] val = processChar(c);
                if (val[0] == 1) {
                    gameOver(true);
                } else {
                    showOnScreen(gs.currentFloor, val[1]);
                    showMouseInfo();
                }
            }
        }
        if (gs.stepsLeft == 0) {
            gameOver(false);
        }
    }

    private TETile[][] playGameWithString(String input) {
        if (input.length() == 0) {
            return gs.allWorld[gs.currentFloor];
        }
        for (int i = 0; i < input.length(); i += 1) {
            processChar(input.charAt(i));
        }
        return gs.allWorld[gs.currentFloor];
    }

    private void showMouseInfo() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY() - bottom;
        while (!StdDraw.hasNextKeyTyped()) {
            int xx = (int) StdDraw.mouseX();
            int yy = (int) StdDraw.mouseY() - bottom;
            if (x == xx && y == yy) {
                continue;
            }
            x = xx;
            y = yy;
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT - bottom) {
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.NOTHING)) {
                    printMouseInfo("This is Nothing!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.PLAYER)) {
                    printMouseInfo("This is Player!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.WATER)) {
                    printMouseInfo("This is Water!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.LOCKED_DOOR)) {
                    printMouseInfo("This is Exit!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.WALL)) {
                    printMouseInfo("This is Wall!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.FLOOR)) {
                    printMouseInfo("This is Floor!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.UPSTAIRS)) {
                    printMouseInfo("This is Upstairs!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.DOWNSTAIRS)) {
                    printMouseInfo("This is Downstairs!");
                }
                if (gs.allWorld[gs.currentFloor][x][y].equals(Tileset.KEY)) {
                    printMouseInfo("This is key!");
                }
            } else {
                printMouseInfo("");
            }
        }
    }

    private void printMouseInfo(String s) {
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(WIDTH - 10.0, bottom / 2.0, 7, 1);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH - 10.0, bottom / 2.0, s);
        StdDraw.show();
    }

    private void gameOver(boolean gameResult) {
        StdDraw.clear(Color.black);
        StdDraw.setFont(largeFont);
        StdDraw.setPenColor(Color.white);
        if (gameResult) {
            StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Congratulations! You win the game!");
        } else {
            StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Game Over! No steps left.");
        }
        StdDraw.show();
    }

    public static void writeGame(GameState game) {
        File f = new File(file);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(game);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public static GameState readGame() {
        File f = new File(file);
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream oi = new ObjectInputStream(fs);
                GameState game = (GameState) oi.readObject();
                oi.close();
                return game;
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found!");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("Class Not Found!");
                System.exit(0);
            }
        }
        return null;
    }


}


