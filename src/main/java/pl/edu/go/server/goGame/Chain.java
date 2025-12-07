package server.goGame;

import java.util.HashSet;
import java.util.Set;

public class Chain {

    private final GoGame.Color color;
    private final Set<Stone> stones = new HashSet<>();
    private final Set<Point> liberties = new HashSet<>();

    public Chain(GoGame.Color color) {
        this.color = color;
    }

    public void addStone(Stone s) {
        stones.add(s);
        s.setChain(this); // przypisz referencję kamienia do tego chaina
    }

    public GoGame.Color getColor() {
        return color;
    }

    public Set<Stone> getStones() {
        return stones;
    }

    public Set<Point> getLiberties() {
        return liberties;
    }

    public void addLiberty(Point p) {
        liberties.add(p);
    }

    public void removeLiberty(Point p) {
        liberties.remove(p);
    }

    public boolean isDead() {
        return liberties.isEmpty();
    }

    public void merge(Chain other) {
        for (Stone s : other.getStones()) {
            addStone(s); // ustawia chain kamienia na ten
        }
        liberties.addAll(other.getLiberties());
    }
}
