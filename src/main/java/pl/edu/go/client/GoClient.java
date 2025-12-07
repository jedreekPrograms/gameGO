package pl.edu.go.client;

 // twoja implementacja
import pl.edu.go.client.networkInterfaces.MessageListener;
import pl.edu.go.client.networkInterfaces.ServerAPI;
import pl.edu.go.client.networkInterfaces.SocketServerAPI;
import pl.edu.go.model.Board;
import pl.edu.go.model.Color;

import java.io.IOException;
import java.util.Scanner;

public class GoClient {
    private final ServerAPI api;
    private final ConsoleUI ui;
    private Board localBoard;
    private Color myColor = null;
    private boolean running = true;

    public GoClient(String host, int port) throws IOException {
        this.api = new SocketServerAPI(host, port);
        this.ui = new ConsoleUI(new Scanner(System.in));
        this.localBoard = null; //stworzymy przy gamestart jak znamy rozmiar
    }

    public void run() {
        api.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(String message) {
                handleServerMessage(message);
            }
        });

        Scanner scanner = new Scanner(System.in);
        while(running) {
            try {
                Board boardForInput = localBoard == null ? new Board(9) : localBoard;
                Color promptColor = myColor == null ? Color.BLACK : myColor;

                String command = ui.getMoveCommand(promptColor, boardForInput);
                api.send(command);
                if (command.equalsIgnoreCase("RESIGN")) {
                    System.out.println("You resigned. Exiting");
                    running = false;
                    break;
                }
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
                break;
            }
            api.close();
            scanner.close();
        }
    }

    private void handleServerMessage(String message) {
        if (message == null) return;

        if (message.startsWith("START")) {
            String[] parts = message.split("\\s+");

            if (parts.length >= 2) {
                myColor = parts[1].equalsIgnoreCase("WHITE") ? Color.WHITE : Color.BLACK;
                System.out.println("Assigned color: " + myColor);
            }
            return;
        }

        if (message.startsWith("WAITING_FOR_OPPONENT")) {
            System.out.println("Waiting for opponent...");
            return;
        }

        if (message.startsWith("BOARD")) {
            // format: BOARD\n<size>\n<line1>\n<line2>...
            String payload = message.substring("BOARD".length()).trim();
            if (payload.isEmpty()) {
                // możliwe, że serwer wysłał BOARD i dalej kolejne linie - w prostym protokole serwer może wysłać BOARD i od razu kolejne linie
                // tutaj uproszczenie: wyświetlimy to co mamy
                System.out.println("BOARD (empty payload)");
                return;
            }
            String[] lines = payload.split("\n");
            try {
                int size = Integer.parseInt(lines[0].trim());
                Board b = new Board(size);
                for (int y = 0; y < size && y + 1 < lines.length; y++) {
                    String row = lines[y + 1];
                    for (int x = 0; x < size && x < row.length(); x++) {
                        char ch = row.charAt(x);
                        if (ch == 'B') b.getClass(); // no-op to avoid unused
                        if (ch == 'B') {
                            // ustaw bezpośrednio (nie ma setterów) — refleksja lub metoda pomocnicza; prostsze: użyj miejsca poprzez move application?
                        }
                    }
                }
                // prostsze: zamiast próbować modyfikować wewnętrzne pole — stwórz obiekt Board i ustaw poprzez miejscowe przypisanie
                // ponieważ Board nie ma publicznych setterów do pojedynczych punktów, w tej wersji wyświetlimy payload
                System.out.println("Board from server:\n" + payload);
            } catch (NumberFormatException e) {
                System.out.println("Board (no size):\n" + payload);
            }
            return;
        }

        if (message.startsWith("VALID")) {
            System.out.println("Move accepted.");
            return;
        }

        if (message.startsWith("INVALID")) {
            System.out.println("Invalid move.");
            return;
        }

        if (message.startsWith("OPPONENT_MOVED")) {
            System.out.println("Opponent moved: " + message.substring("OPPONENT_MOVED".length()).trim());
            return;
        }

        if (message.startsWith("OPPONENT_PASSED")) {
            System.out.println("Opponent passed.");
            return;
        }

        if (message.startsWith("RESIGN")) {
            System.out.println("RESIGN: " + message.substring(7));
            return;
        }

        if (message.startsWith("WINNER")) {
            System.out.println("Game winner: " + message.substring("WINNER".length()).trim());
            running = false;
            return;
        }

        if (message.startsWith("ERROR")) {
            System.out.println("Server error: " + message.substring(6));
            return;
        }

        System.out.println("Server: " + message);
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try {
            GoClient client = new GoClient(host, port);
            client.run();
        } catch (IOException e) {
            System.err.println("Nie udało się połączyć z serwerem: " + e.getMessage());
        }
    }
}
