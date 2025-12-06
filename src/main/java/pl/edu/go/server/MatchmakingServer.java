package server;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import server.networkInterfaces.ClientConnection;

public class MatchmakingServer {

    private static final int PORT = 5000;

    // Kolejka oczekujących graczy
    private static final LinkedBlockingQueue<ClientConnection> waitingPlayers = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("Serwer matchmakingu uruchomiony na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                //System.out.println("Nowy klient połączony: " + socket);

                ClientHandler handler = new ClientHandler(socket);
                handler.setMessageListener(msg -> System.out.println("Odebrano od " + handler.getSocket() + ": " + msg));
                Thread t = new Thread(handler);
                t.start();

                // Dodajemy klienta do kolejki oczekujących
                matchmaking(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Próba sparowania graczy
    private static synchronized void matchmaking(ClientConnection newPlayer) {
        try {
            // jeśli już jest ktoś w kolejce – parujemy
            if (!waitingPlayers.isEmpty()) {
                ClientConnection player1 = waitingPlayers.poll();
                ClientConnection player2 = newPlayer;

                if (player1 != null && player2 != null) {
                    System.out.println("Parowanie graczy: " + player1.getSocket() + " <-> " + player2.getSocket());

                    // Parowanie i nadanie kolorów
                    player1.setPartner(player2);
                    player2.setPartner(player1);

                    player1.send("WHITE");
                    player2.send("BLACK");

                    player1.send("GAME_START");
                    player2.send("GAME_START");
                }

            } else {
                // nikt nie czeka – ustawiamy klienta jako oczekującego
                waitingPlayers.add(newPlayer);
                newPlayer.send("WAITING_FOR_OPPONENT");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
