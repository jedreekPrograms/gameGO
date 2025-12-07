package pl.edu.go.client;

 // twoja implementacja
import pl.edu.go.client.networkInterfaces.MessageListener;
import pl.edu.go.client.networkInterfaces.ServerAPI;
import pl.edu.go.model.Board;
import pl.edu.go.model.Color;

import java.io.IOException;
import java.util.Scanner;

public class GoClient {
    private final ServerAPI api;
    private final ConsoleUI ui;
    private Board localBoard;
    private Color myColor = null;
    private boolean gameStarted = false;

    public GoClient(ServerAPI api, ConsoleUI ui) {
        this.api = api;
        this.ui = ui;
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
        while(true) {
            try {
                String command = ui.getMoveCommand(
                        myColor == null ? Color.BLACK : myColor,
                        localBoard == null ? new Board(9) : localBoard
                );
                api.send(command);
                if (command.equalsIgnoreCase("RESIGN")) {
                    System.out.println("You resigned. Exiting");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
                break;
            }
            api.close();
            scanner.close();
            System.exit(0);
        }
    }

    private void handleServerMessage(String message) {
        if (message == null) return;

        if (message.startsWith("COLOR")) {
            String[] parts = message.split(" ");
            if (parts.length >= 2) {
                String c = parts[1].trim();
                myColor = c.equalsIgnoreCase("WHITE") ? Color.WHITE : Color.BLACK;
                System.out.println("Assigned color: " + myColor);
            }
            return;
        }

        if (message.startsWith("GAME_START")) {
            gameStarted = true;
            System.out.println("Game started!");
            return;
        }

        if (message.startsWith("WAITING_FOR_OPPONENT")) {
            System.out.println("Waiting for opponent...");
            return;
        }

        if (message.startsWith("BOARD")) {
            String boardText = message.substring("BOARD".length()).trim();
            System.out.println("Board:\n" + boardText);
            return;
        }

        if (message.startsWith("ERROR")) {
            System.out.println("Server error: " + message.substring(6));
            return;
        }

        if (message.startsWith("PASS")) {
            System.out.println("PASS: " + message.substring(5));
            return;
        }

        if (message.startsWith("RESIGN")) {
            System.out.println("RESIGN: " + message.substring(7));
            return;
        }

        System.out.println("Server: " + message);
    }
}
