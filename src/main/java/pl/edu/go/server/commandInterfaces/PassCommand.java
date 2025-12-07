package server.commandInterfaces;

import server.GameSession;
import server.networkInterfaces.ClientConnection;

public class PassCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        session.sendToBoth("PASS " + session.getPlayerColor(sender));
        return true;
    }
}
