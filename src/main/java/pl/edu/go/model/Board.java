package pl.edu.go.model;

import java.util.*;

public class Board {
    private final int size;
    private final Color[][] grid;

    private final RulesEngine rules = RulesEngine.getInstance();

    public Board(int size) {
        this.size = size;
        this.grid = new Color[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                grid[x][y] = Color.EMPTY;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Color get(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return null;
        return grid[x][y];
    }

    public boolean isEmpty(int x, int y) {
        Color c = get(x, y);
        return c == Color.EMPTY;
    }

    public int placeStone(Color color, int x, int y) {

        if (x < 0 || x >= size || y < 0 || y >= size) return -1;
        if (!isEmpty(x, y)) return -1;

        grid[x][y] = color;

        int totalCaptured = 0;

        Color enemy = color.opponent();

        // sprawdzamy grupy przeciwnika
        for (Point p : getAdjacentPoints(x, y)) {
            if (get(p.x, p.y) == enemy) {
                Set<Point> enemyGroup = rules.getGroup(this, p.x, p.y);
                Set<Point> liberties = rules.getLiberties(this, enemyGroup);

                if (liberties.isEmpty()) {
                    totalCaptured += removeGroup(enemyGroup);
                }
            }
        }

        // sprawdzamy czy nasza grupa nie jest samobójstwem
        Set<Point> myGroup = rules.getGroup(this, x, y);
        Set<Point> myLiberties = rules.getLiberties(this, myGroup);

        if (myLiberties.isEmpty() && totalCaptured == 0) {
            grid[x][y] = Color.EMPTY;
            return -1;
        }

        return totalCaptured;
    }

    public int removeGroup(Set<Point> group) {
        for (Point p: group) {
            grid[p.x][p.y] = Color.EMPTY;
        }
        return group.size();
    }

    public List<Point> getAdjacentPoints(int x, int y) {
        List<Point> list = new ArrayList<>();

        if(x > 0) list.add(new Point(x - 1, y));
        if (x < size - 1) list.add(new Point(x + 1, y));
        if (y > 0) list.add(new Point(x, y - 1));
        if (y < size - 1) list.add(new Point(x, y + 1));

        return list;
    }

    public int computeHash() {
        int hash = 1;
        int prime = 31;

        for (int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                Color c = grid[x][y];

                int val = switch (c) {
                    case EMPTY -> 0;
                    case BLACK -> 1;
                    case WHITE -> 2;
                };

                hash = hash * prime + val;
            }
        }
        return hash;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Color c = get(x, y);
                char ch = switch (c) {
                    case EMPTY -> '.';
                    case BLACK -> 'B';
                    case WHITE -> 'W';
                };
                sb.append(ch);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
