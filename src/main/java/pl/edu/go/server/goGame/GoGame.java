package server.goGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import server.goGame.Board;
public class GoGame {

    private Color currentTurn = Color.WHITE;
    public static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};


    public static final int size = 9;
    public enum Color { BLACK, WHITE;
        public static Color opposite(Color c) {
            return c == BLACK ? WHITE : BLACK;
        }
    }
    private final Board board;
    private final Set<Chain> chains = new HashSet<>();
    private final ChainManager chainManager;

    public GoGame(int size) {
        this.board = new Board(size);
        this.chainManager = new ChainManager(this);
    }

    public Board getBoard() {
        return board;
    }

    public synchronized boolean placeStone(int x, int y, Color color) {
    if (!board.isOnBoard(x, y)) return false;
    if (!board.isEmpty(x, y)) return false;

    Stone newStone = new Stone(x, y, color);
    board.placeStone(newStone);

    chainManager.updateChainsAfterMove(newStone);
    return true;
    }

    public synchronized List<Chain> getNeighborChains(int x, int y, Color color) {
        List<Chain> result = new ArrayList<>();

        for (Chain c : chains) {
            if (c.getColor() != color) continue;

            for (Stone s : c.getStones()) {
                if (Math.abs(s.x - x) + Math.abs(s.y - y) == 1) {
                    result.add(c);
                    break;
                }
            }
        }
        return result;
    }

    public synchronized void registerChain(Chain chain) {
        chains.add(chain);
    }

    public synchronized void removeChain(Chain chain) {
        for (Stone s : chain.getStones()) {
            board.removeStone(s.x, s.y);
        }
        chains.remove(chain);
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public void switchTurn() {
        currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public String renderBoard() {
        return board.toAscii();
    }
    public synchronized void unregisterChain(Chain chain) {
    chains.remove(chain); // tylko usuń z zestawu chainów, nie ruszaj kamieni
    }
}
