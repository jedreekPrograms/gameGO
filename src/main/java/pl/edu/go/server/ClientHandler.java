package server;
import java.io.*;
import java.net.*;

import server.networkInterfaces.MessageListener;
import server.networkInterfaces.ClientConnection;
//Domyslna implementacja komunikacji z klientem i obslugi klientow
public class ClientHandler implements Runnable, ClientConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener listener;
    private ClientConnection partner; // drugi gracz w parze
    private GameSession session; // nowa rzecz
    private GoGame.Color assignedColor;


    public ClientHandler(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Błąd inicjalizacji klienta");
        }
    }

    public void setGameSession(GameSession session, GoGame.Color color) {
        this.session = session;
        this.assignedColor = color;
        send("COLOR " + color.name());
    }

    @Override
    public void run() {
        try {
            String msg;

            while ((msg = in.readLine()) != null) {

            if (session != null) {
                session.onMessage(this, msg);
                }
            }

        } catch (IOException e) {
            System.out.println("Klient rozłączony: " + socket);
        } finally {
            close();
        }
    }
    
    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void setPartner(ClientConnection partner) {
        this.partner = partner;
    }

    @Override
    public void send(String msg) {
        out.println(msg);
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
