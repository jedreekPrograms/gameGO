package server;

import server.commandInterfaces.MoveCommand;
import server.commandInterfaces.PassCommand;
import server.commandInterfaces.ResignCommand;
import server.networkInterfaces.ClientConnection;

public class GameSession {

    private final ClientConnection whitePlayer;
    private final ClientConnection blackPlayer;
    private final GoGame game;
    private final CommandRegistry registry;
    private GoGame.Color currentTurn = GoGame.Color.WHITE;

    public GameSession(ClientConnection whitePlayer, ClientConnection blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.game = new GoGame(9);
        this.registry = new CommandRegistry();
        registry.register("MOVE", new MoveCommand());
        registry.register("PASS", new PassCommand());
        registry.register("RESIGN", new ResignCommand());
        sendToBoth("GAME_START");
        sendToBoth(game.renderBoard());
    }

    public void onMessage(ClientConnection sender, String message) {
        if (!isPlayerTurn(sender)) {
            sender.send("ERROR Not your turn");
            return;
        }

        boolean success = registry.execute(message, this, sender);
        if(success){
        switchTurn();
        }

    }

    public GoGame getGame() {
        return game;
    }

    public GoGame.Color getPlayerColor(ClientConnection c) {
        return c == whitePlayer ? GoGame.Color.WHITE : GoGame.Color.BLACK;
    }

    public boolean isPlayerTurn(ClientConnection c) {
        return getPlayerColor(c) == currentTurn;
    }

    public void switchTurn() {
        currentTurn = currentTurn == GoGame.Color.WHITE ?
                GoGame.Color.BLACK : GoGame.Color.WHITE;
    }

    public void sendToBoth(String msg) {
        whitePlayer.send(msg);
        blackPlayer.send(msg);
    }
    public void endSession(){

    }
}
