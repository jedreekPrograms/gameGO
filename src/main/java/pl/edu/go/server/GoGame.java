package server;

public class GoGame {

    private final int size;
    private final char[][] board;
    private Color currentTurn = Color.WHITE;

    public enum Color {
        WHITE, BLACK
    }

    public GoGame(int size) {
        this.size = size;
        this.board = new char[size][size];

        // pustą planszę wypełniamy kropkami
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '.';
            }
        }
    }

    public boolean placeStone(int x, int y, Color color) {
        if (x < 0 || x >= size || y < 0 || y >= size) return false;
        if (board[x][y] != '.') return false; // pole zajęte

        board[x][y] = (color == Color.WHITE ? 'W' : 'B');
        return true;
    }

    public char[][] getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public void switchTurn() {
        currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public String renderBoard() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char c : row) sb.append(c);
            sb.append("\n");
        }
        return sb.toString();
    }
}
