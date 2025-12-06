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

    public ClientHandler(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Błąd inicjalizacji klienta");
        }
    }

    @Override
    public void run() {
        try {
            String msg;

            while ((msg = in.readLine()) != null) {

                System.out.println("[" + socket + "] " + msg);
                if (listener != null) {
                    listener.onMessage(msg);
                }
                // komunikacja TYLKO w ramach pary
                if (partner != null) {
                    partner.send(msg);
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
