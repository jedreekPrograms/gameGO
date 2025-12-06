package pl.edu.go.client;

import pl.edu.go.model.Move;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkHandler {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public NetworkHandler(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Wysyła ruch do serwera.
     *
     * @param move obiekt Move do wysłania
     * @throws IOException jak będzie problem z wysłaniem
     */
    public void sendMove(Move move) throws IOException {
        out.writeObject(move);
        out.flush();
    }
}
