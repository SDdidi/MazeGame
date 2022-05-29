package byog.Core;

import java.util.LinkedList;
import java.util.List;

public class Room {
    //p1 is left-bottom point, p2 is right-top point.
    Position p1, p2;
    List<Breakpoint> BPS;

    public Room(Breakpoint bp, Position p1, Position p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.BPS = new LinkedList<Breakpoint>();
        this.BPS.add(bp);
    }

    public Room(Breakpoint bp, Position p1, int roomWidth, int roomHeight) {
        this(bp, p1, new Position(p1.x + roomWidth, p1.y + roomHeight));
    }

    public Room(Breakpoint bp, int roomWidth, int roomHeight, Position p2) {
        this(bp, new Position(p2.x - roomWidth, p2.y - roomHeight), p2);
    }


    public Room(Room other) {
        this.p1 = new Position(other.p1);
        this.p2 = new Position(other.p2);
        this.BPS = new LinkedList<Breakpoint>();
        for (Breakpoint pp : other.BPS) {
            this.BPS.add(pp);
        }
    }

    public boolean overlap(Room room) {
        if (p1.equals(room.p1) && p2.equals(room.p2)) {
            return true;
        }
        if (contain(room.p1) || contain(room.p2) ||
                contain(new Position(room.p1.x, room.p2.y)) || contain(new Position(room.p2.x, room.p1.y))) {
            return true;
        }
        if (room.p1.x >= p1.x && room.p2.x <= p2.x && room.p1.y < p1.y && room.p2.y > p2.y) {
            return true;
        }
        if (room.p1.y >= p1.y && room.p2.y <= p2.y && room.p1.x < p1.x && room.p2.x > p2.x) {
            return true;
        }
        if (room.p1.y < p1.y && room.p2.y > p2.y &&
                ((room.p1.x >= p1.x && room.p1.x <= p2.x) || (room.p2.x >= p1.x && room.p2.x <= p2.x))) {
            return true;
        }
        if (room.p1.x < p1.x && room.p2.x > p2.x &&
                ((room.p1.y >= p1.y && room.p1.y <= p2.y) || (room.p2.y >= p1.y && room.p2.y <= p2.y))) {
            return true;
        }
        return false;
    }

    public boolean contain(Position p) {
        if ((p.x >= p1.x && p.x <= p2.x) && (p.y >= p1.y && p.y <= p2.y)) {
            return true;
        }
        return false;
    }
}

// breakP is the positon on th wall that is floor. direction denotes its direction, which should always point
// to the outer space.direction[0]={-1,0,+1},direction[1]={-1,0,+1}.abs(direction[0])+abs(direction[1])=1.
class Breakpoint {
    Position breakP;
    int[] direction;
    int roomIndex = -1;

    Breakpoint(Position p, int[] d) {
        breakP = new Position(p);
        direction = new int[2];
        direction[0] = d[0];
        direction[1] = d[1];
    }

    Breakpoint(Breakpoint other) {
        this(other.breakP, other.direction);
    }

    Breakpoint getTwin() {
        int[] newDirection = new int[2];
        newDirection[0] = -direction[0];
        newDirection[1] = -direction[1];
        Position newBreakP = new Position(breakP.x + direction[0], breakP.y + direction[1]);
        return new Breakpoint(newBreakP, newDirection);
    }
}
