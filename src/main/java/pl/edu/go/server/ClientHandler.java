import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ClientHandler partner; // drugi gracz w parze

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

    public void setPartner(ClientHandler partner) {
        this.partner = partner;
    }

    public void send(String msg) {
        out.println(msg);
    }

    public Socket getSocket() {
        return socket;
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
