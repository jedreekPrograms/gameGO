package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.Position;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class MoveCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        if (args.length != 2) {
            sender.send("ERROR MOVE requires x y");
            return false;
        }

        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        Color color = session.getPlayerColor(sender);

        Move move = Move.place(new Position(x, y), color);

        boolean ok = session.getGame().applyMove(move);

        if (!ok) {
            sender.send("ILLEGAL MOVE");
            return false;
        }

        session.sendToBoth("MOVE " + color + " " + x + " " + y);
        session.sendToBoth("BOARD\n" + session.getGame().getBoard().toString());

        return true;
    }
}
