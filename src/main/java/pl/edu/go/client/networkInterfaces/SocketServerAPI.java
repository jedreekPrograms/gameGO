package client.networkInterfaces;
import java.io.*;
import java.net.*;
//Domyślna implementacja interfejsu ServerAPI
public class SocketServerAPI implements ServerAPI {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private MessageListener listener;

    public SocketServerAPI(String host, int port) throws IOException {
        socket = new Socket(host, port);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Wątek odbioru danych
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (listener != null) {
                        listener.onMessage(msg);
                    }
                }
            } catch (IOException ignored) {}
        }).start();
    }

    @Override
    public void send(String message) {
        out.println(message);
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
