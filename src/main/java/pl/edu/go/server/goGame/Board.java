package server.goGame;

import java.util.*;
import server.goGame.Point;
public class Board {
    private final Stone[][] grid;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.grid = new Stone[size][size];
    }

    public boolean isOnBoard(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    public boolean isEmpty(int x, int y) {
        return grid[x][y] == null;
    }

    public void placeStone(Stone stone) {
        grid[stone.x][stone.y] = stone;
    }

    public void removeStone(int x, int y) {
        grid[x][y] = null;
    }

    public Stone getStone(int x, int y) {
        return grid[x][y];
    }

    public String toAscii() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Stone s = grid[x][y];
                if (s == null) sb.append(".");
                else sb.append(s.color == GoGame.Color.BLACK ? "B" : "W");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
