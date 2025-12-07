package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class PassCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        Color color = session.getPlayerColor(sender);
        Move move = Move.pass(color);

        session.getGame().applyMove(move);

        session.sendToBoth("PASS " + color);
        return true;
    }
}
