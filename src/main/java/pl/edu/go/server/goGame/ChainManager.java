package server.goGame;

import java.util.*;

public class ChainManager {

    private final GoGame game;

    public ChainManager(GoGame game) {
        this.game = game;
    }

    public void updateChainsAfterMove(Stone newStone) {
    // 1. Zbierz wszystkie sąsiadujące chainy tego samego koloru
    Set<Chain> neighborChains = new HashSet<>();
    for (Point p : getNeighbors(newStone)) {
        Stone s = game.getBoard().getStone(p.x, p.y);
        if (s != null && s.color == newStone.color && s.getChain() != null) {
            neighborChains.add(s.getChain());
        }
    }

    // 2. Utwórz nowy chain i scal wszystkie sąsiadujące
    Chain mergedChain = new Chain(newStone.color);
    mergedChain.addStone(newStone);

    for (Chain c : neighborChains) {
        mergedChain.merge(c);
        game.unregisterChain(c); // TYLKO usuń referencję w GoGame.chains, nie kamienie z planszy
    }

    recomputeLiberties(mergedChain);
    game.registerChain(mergedChain);

    // 3. Sprawdź wszystkie sąsiadujące chainy przeciwnika
    Set<Chain> enemyChains = new HashSet<>();
    for (Point p : getNeighbors(newStone)) {
        Stone s = game.getBoard().getStone(p.x, p.y);
        if (s != null && s.color != newStone.color && s.getChain() != null) {
            enemyChains.add(s.getChain());
        }
    }

    List<Chain> deadChains = new ArrayList<>();
    for (Chain enemy : enemyChains) {
        recomputeLiberties(enemy);
        if (enemy.isDead()) deadChains.add(enemy);
    }

    // 4. Usuń martwe chainy (tutaj usuwamy kamienie z planszy)
    for (Chain dead : deadChains) {
        game.removeChain(dead);
    }
}

    private void recomputeLiberties(Chain chain) {
        chain.getLiberties().clear();
        for (Stone s : chain.getStones()) {
            for (int[] d : GoGame.DIRS) {
                int nx = s.x + d[0];
                int ny = s.y + d[1];
                Point np = new Point(nx, ny);
                if (game.getBoard().isOnBoard(nx, ny) && game.getBoard().isEmpty(nx, ny)) {
                    chain.addLiberty(np);
                }
            }
        }
    }

    private List<Point> getNeighbors(Stone s) {
        List<Point> neighbors = new ArrayList<>();
        for (int[] d : GoGame.DIRS) {
            int nx = s.x + d[0];
            int ny = s.y + d[1];
            if (game.getBoard().isOnBoard(nx, ny)) {
                neighbors.add(new Point(nx, ny));
            }
        }
        return neighbors;
    }
}
