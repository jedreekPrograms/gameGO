package server.goGame;

public class Stone {
    public final int x;
    public final int y;
    public final GoGame.Color color;
    private Chain chain; // referencja do łańcucha

    public Stone(int x, int y, GoGame.Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.chain = null;
    }

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }
}