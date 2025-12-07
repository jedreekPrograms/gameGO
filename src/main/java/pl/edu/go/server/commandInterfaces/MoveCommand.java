package server.commandInterfaces;

import server.GameSession;
import server.networkInterfaces.ClientConnection;

public class MoveCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        if (args.length != 2) {
            sender.send("ERROR MOVE requires x y");
            return false;
        }

        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        
        session.getGame().placeStone(x, y, session.getPlayerColor(sender));

        session.sendToBoth("BOARD\n" + session.getGame().renderBoard());
        return true;
    }
}
