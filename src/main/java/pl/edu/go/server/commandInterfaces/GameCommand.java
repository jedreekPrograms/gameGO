package server.commandInterfaces;
import server.GameSession;
import server.networkInterfaces.ClientConnection;

public interface GameCommand {
    boolean execute(String[] args, GameSession session, ClientConnection sender);
}
