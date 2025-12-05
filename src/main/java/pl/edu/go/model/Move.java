package pl.edu.go.model;

public class Move {
    //place postawienie np (3,5)
    //pass to ze rezygunuje z ruchu
    //resign poddaje sie
    public enum Type {PLACE, PASS, RESIGN}

    private final Type type;
    private final Position pos;
    private final Color color;

    public Move(Type type, Position pos, Color color) {
        this.type = type;
        this.pos = pos;
        this.color = color;
    }

    public static Move place(Position pos, Color color) {
        return new Move(Type.PLACE, pos, color);
    }

    public static Move pass(Color color) {
        return new Move(Type.PASS, null, color);
    }

    public static Move resign (Color color) {
        return new Move(Type.RESIGN, null, color);
    }

    public Type getType() {
        return type;
    }

    public Position getPos() {
        return pos;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        switch (type) {
            case PLACE: return color + " ->" + pos;
            case PASS: return color + " passes";
            case RESIGN: return color + " resigns";
            default: return "";
        }
    }
}
