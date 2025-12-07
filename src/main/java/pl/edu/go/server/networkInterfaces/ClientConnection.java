package server.networkInterfaces;

import java.net.Socket;

import server.GameSession;
import server.GoGame;
//Interfejs do komunikacji serwera z klientem
public interface ClientConnection {

    // wysyła wiadomość do klienta
    void send(String message);

    // rejestruje listener odbierający wiadomości od klienta
    void setMessageListener(MessageListener listener);

    // zwraca identyfikator klienta lub nazwę
    Socket getSocket();

    // zamknięcie połączenia
    void close();

    void setPartner(ClientConnection partner);

    void setGameSession(GameSession session, GoGame.Color color);
}
