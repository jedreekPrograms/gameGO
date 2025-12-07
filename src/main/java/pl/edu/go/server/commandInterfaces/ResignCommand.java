package server.commandInterfaces;

import server.GameSession;
import server.GoGame;
import server.networkInterfaces.ClientConnection;

public class ResignCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        GoGame.Color loser = session.getPlayerColor(sender);
        GoGame.Color winner = loser == GoGame.Color.BLACK ? GoGame.Color.WHITE : GoGame.Color.BLACK;

        session.sendToBoth("RESIGN " + loser);
        session.sendToBoth("WINNER " + winner);

        session.endSession();
        return true;
    }
}
