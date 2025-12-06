package pl.edu.go.model;

import java.util.*;

public class Board {
    private final int size;
    private final Color[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Color[size][size];
    }

    public int getSize() {
        return size;
    }

    public Color get(int x, int y) {
        return grid[x][y];
    }

    private boolean isEmpty(int x, int y) {
        return grid[x][y] == Color.EMPTY;
    }

    int[][] moves = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };


    public int placeStone(Color color, int x, int y) {
        if (!isEmpty(x, y)) {
            return -1;
        }

        grid[x][y] = color;

        int totalCaptured = 0;

        Color enemy;

        if (color == Color.BLACK) {
            enemy = Color.WHITE;
        } else {
            enemy = Color.BLACK;
        }

        for (Point p : getAdjacentPoints(x, y)) {
            if (get(p.x, p.y) == enemy) {
                Set<Point> enemyGroup = getGroup(p.x, p.y);
                Set<Point> liberties = getLiberties(enemyGroup);

                if(liberties.isEmpty()) {
                    totalCaptured += removeGroup(enemyGroup);
                }
            }
        }

        Set<Point> myGroup = getGroup(x, y);
        Set<Point> myLiberties = getLiberties(myGroup);

        if (myLiberties.isEmpty() && totalCaptured == 0) {
            grid[x][y] = null;
            return -1;
        }

        return totalCaptured;
    }

    public Set<Point> getGroup(int x, int y) {
        Set<Point> group = new HashSet<>();
        Color color = grid[x][y];

        if (color == Color.EMPTY) {
            return group;
        }

        Deque<Point> stack = new ArrayDeque<>();
        stack.push(new Point(x, y));
        group.add(new Point(x, y));

        while(!stack.isEmpty()) {
            Point p = stack.pop();
            for (Point n : getAdjacentPoints(p.x, p.y)) {
                if (get(n.x, n.y) == color && !group.contains(n)) {
                    group.add(n);
                    stack.push(n);
                }
            }
        }

        return group;
    }

    public Set<Point> getLiberties(Set<Point> group) {
        Set<Point> liberties = new HashSet<>();

        for(Point p : group) {
            for (Point n : getAdjacentPoints(p.x, p.y)) {
                if (isEmpty(n.x, n.y)) {
                    liberties.add(n);
                }
            }
        }

        return liberties;
    }

    public int removeGroup(Set<Point> group) {
        for (Point p: group) {
            grid[p.x][p.y] = null;
        }
        return group.size();
    }

    private List<Point> getAdjacentPoints(int x, int y) {
        List<Point> list = new ArrayList<>();

        if(x > 0) list.add(new Point(x - 1, y));
        if (x < size - 1) list.add(new Point(x + 1, y));
        if (y > 0) list.add(new Point(x, y - 1));
        if (y < size - 1) list.add(new Point(x, y + 1));

        return list;
    }

    public int computeHash() {
        int hash = 0;
        int prime = 31;

        for (int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                Color c = grid[x][y];

                int val;
                if (c == null) {
                    val = 0;
                } else {
                    if (c == Color.BLACK) {
                        val = 1;
                    } else {
                        val = 2;
                    }
                }

                hash = hash * prime + val;
            }
        }
        return hash;
    }
}
