package chatapplication;

import chatapplication.server.networking.ChatServer;

public class Main {

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.run();
    }

}
