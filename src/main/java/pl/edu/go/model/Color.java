package pl.edu.go.model;

public enum Color {
    EMPTY, BLACK, WHITE;

    public Color opponent() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }
}
