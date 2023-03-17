package chatapplication.server.networking;

import chatapplication.common.models.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable {
    final static int PORT = 8888;
    private static ArrayList<RequestHandler> clients;
    private ExecutorService pool;
    private ServerSocket server;
    private static ArrayList<User> users;
    private User user;

    public ChatServer(){
        clients = new ArrayList<>();
        users = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(PORT);
            pool = Executors.newCachedThreadPool();

            while(true) {
                Socket userSocket = server.accept();

                user = new User();
                RequestHandler handler = new RequestHandler(user);

                user.setSocket(userSocket);
                user.setHandler(handler);
                users.add(user);

                pool.execute(handler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<User> getUsers(){
        return users;
    }

}
