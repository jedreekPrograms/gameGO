package server;

import java.util.HashMap;
import java.util.Map;

import server.commandInterfaces.GameCommand;
import server.networkInterfaces.ClientConnection;

public class CommandRegistry {

    private final Map<String, GameCommand> commands = new HashMap<>();

    public void register(String name, GameCommand command) {
        commands.put(name.toUpperCase(), command);
    }

    public boolean execute(String raw, GameSession session, ClientConnection sender) {
        String[] parts = raw.split(" ");
        String name = parts[0].toUpperCase();
        String[] args = parts.length > 1 ? raw.substring(name.length()).trim().split(" ") : new String[0];

        GameCommand cmd = commands.get(name);
        if (cmd != null) {
            return cmd.execute(args, session, sender);
        } else {
            sender.send("ERROR Unknown command: " + name);
            return false;
        }
    }
}
