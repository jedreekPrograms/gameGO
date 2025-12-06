import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {

            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Połączono z serwerem. Możesz pisać wiadomości:");

            // Wątek odbierający wiadomości
            new Thread(() -> {
                String msg;
                try {
                    while ((msg = serverIn.readLine()) != null) {
                        System.out.println("Odebrano: " + msg);
                    }
                } catch (IOException ignored) {}
            }).start();

            // Wysyłanie wiadomości
            String text;
            while ((text = userIn.readLine()) != null) {
                serverOut.println(text);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
