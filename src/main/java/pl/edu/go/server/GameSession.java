package pl.edu.go.server;

import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Move;
import pl.edu.go.model.Position;
import pl.edu.go.model.Color;
import pl.edu.go.model.Board;

import java.util.Objects;

/**
 * Zarządza pojedynczą sesją gry między dwoma klientami.
 * Używa pl.edu.go.model.GameState (który korzysta z pl.edu.go.model.Board itp.)
 */
public class GameSession {

    private final ClientConnection whitePlayer;
    private final ClientConnection blackPlayer;
    private final GameState game;
    private boolean sessionEnded = false;
    private final CommandRegistry registry;


    public GameSession(ClientConnection whitePlayer, ClientConnection blackPlayer, int boardSize, CommandRegistry registry) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.game = new GameState(boardSize);
        this.registry = registry;

        // Wyślij start do obu - setGameSession już powinno wysłać START <COLOR>
        // Teraz wyślij początkową planszę
        //sendBoardToBoth();
        // Czarny zaczyna (konwencja) -> jeśli chcesz dać informację o turze:
        //sendToPlayer(getClientByColor(Color.BLACK), "YOUR_TURN");
    }

    public void start() {
        sendBoardToBoth();

        ClientConnection black = getClientByColor(Color.BLACK);
        if (black != null) black.send("YOUR_TURN");
    }

    public synchronized void onMessage(ClientConnection sender, String message) {
        if (sessionEnded) {
            sender.send("ERROR Session ended");
            return;
        }

        message = message.trim();
        if (message.isEmpty()) return;

        String[] parts = message.split("\\s+");
        String cmd = parts[0].toUpperCase();

        Color senderColor = getPlayerColor(sender);
        if (senderColor == null) {
            sender.send("ERROR Unknown player");
            return;
        }

        // Sprawdź czy to tura tego gracza dla komend wymagających tury
        boolean requiresTurn = cmd.equals("MOVE") || cmd.equals("PASS");
        if (requiresTurn && !Objects.equals(game.getNextToMove(), senderColor)) {
            sender.send("ERROR Not your turn");
            return;
        }

        switch (cmd) {
            case "MOVE":
                handleMove(sender, parts, senderColor);
                break;

            case "PASS":
                handlePass(sender, senderColor);
                break;

            case "RESIGN":
                handleResign(sender, senderColor);
                break;

            default:
                sender.send("ERROR Unknown command: " + cmd);
        }
    }

    private void handleMove(ClientConnection sender, String[] parts, Color color) {
        if (parts.length < 3) {
            sender.send("ERROR MOVE requires x y");
            return;
        }

        int x, y;
        try {
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            sender.send("ERROR Invalid coordinates");
            return;
        }

        Position pos = new Position(x, y);
        Move m = Move.place(pos, color);

        synchronized (game) {
            boolean ok = game.applyMove(m);
            if (!ok) {
                sender.send("INVALID");
                return;
            }

            // Na sukces: wyślij aktualny stan planszy obu, potwierdź ruch i powiadom przeciwnika
            sender.send("VALID");
            ClientConnection opp = getOpponent(sender);
            if (opp != null) {
                opp.send("OPPONENT_MOVED " + x + " " + y);
                opp.send("YOUR_TURN");
            }
            // Wyślij zserializowaną planszę do obu
            sendBoardToBoth();
        }
    }

    private void handlePass(ClientConnection sender, Color color) {
        Move m = Move.pass(color);
        synchronized (game) {
            boolean ok = game.applyMove(m);
            if (!ok) {
                sender.send("INVALID");
                return;
            }
            sender.send("VALID");
            ClientConnection opp = getOpponent(sender);
            if (opp != null) {
                opp.send("OPPONENT_PASSED " + color.name());
                opp.send("YOUR_TURN");
            }
            sendBoardToBoth();
        }
    }

    private void handleResign(ClientConnection sender, Color color) {
        ClientConnection opp = getOpponent(sender);
        // poinformuj obu, ustaw koniec sesji
        sender.send("RESIGN " + color.name());
        if (opp != null) opp.send("WINNER " + oppestsColor(opp).name());
        endSession();
    }

    private Color oppestsColor(ClientConnection c) {
        Color col = getPlayerColor(c);
        return col == Color.BLACK ? Color.WHITE : Color.BLACK;
    }

    private void sendBoardToBoth() {
        String serialized = serializeBoard(game.getBoard());
        sendToBoth("BOARD\n" + serialized);
    }

    private String serializeBoard(Board b) {
        StringBuilder sb = new StringBuilder();
        int size = b.getSize();
        sb.append(size).append("\n"); // linia z rozmiarem (ułatwia klientowi)
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pl.edu.go.model.Color c = b.get(x, y);
                char ch;
                if (c == null || c == pl.edu.go.model.Color.EMPTY) ch = '.';
                else if (c == pl.edu.go.model.Color.BLACK) ch = 'B';
                else ch = 'W';
                sb.append(ch);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public GameState getGame() {
        return game;
    }

    public Color getPlayerColor(ClientConnection c) {
        if (c == whitePlayer) return Color.WHITE;
        if (c == blackPlayer) return Color.BLACK;
        return null;
    }

    private ClientConnection getOpponent(ClientConnection c) {
        return c == whitePlayer ? blackPlayer : whitePlayer;
    }

    private ClientConnection getClientByColor(Color color) {
        return color == Color.WHITE ? whitePlayer : blackPlayer;
    }

    public void sendToBoth(String msg) {
        if (whitePlayer != null) whitePlayer.send(msg);
        if (blackPlayer != null) blackPlayer.send(msg);
    }

    public void endSession() {
        sessionEnded = true;
        // opcjonalnie zetnij połączenia
        if (whitePlayer != null) whitePlayer.close();
        if (blackPlayer != null) blackPlayer.close();
    }

    public boolean handleCommand(String cmd, String[] args, ClientConnection sender) {
        GameCommand command = registry.get(cmd);

        if (command == null) {
            sender.send("ERROR Unknown command: " + cmd);
            return false;
        }

        return command.execute(args, this, sender);
    }

}
