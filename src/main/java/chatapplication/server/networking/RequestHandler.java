package chatapplication.server.networking;

import chatapplication.common.constants.Commands;
import chatapplication.common.constants.util.IoUtil;
import chatapplication.common.models.GroupChat;
import chatapplication.common.models.User;
import chatapplication.server.ChatRoomType;
import chatapplication.server.database.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class RequestHandler implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private User user;

    public RequestHandler(User user){
        this.user = user;
    }

    private final DatabaseManager dbManager = new DatabaseManager();

    public void sendMessage(String msg){
        out.println(msg);
    }

    public String getUserInput(){
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(String msg){
        for(User user : ChatServer.getUsers()){

            if(user.isLoggedIn()) {
                user.getHandler().sendMessage(msg);
            }
        }
    }


    @Override
    public void run() {

        String userInput;

        String username;
        String password; // TODO: Encrypt

        Commands commands;
        IoUtil ioUtil;

        try{
            in = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
            out = new PrintWriter(user.getSocket().getOutputStream(), true);
            ioUtil = new IoUtil(in, out);

            // Display welcome screen
            ioUtil.displayWelcomeScreen();

            while(!user.isLoggedIn()){
                // Prompt user for username and password
                username = ioUtil.prompt("Enter username: ");
                password = ioUtil.prompt("Enter password: ");


                if (dbManager.isReturningUser(username)) {
                    if (dbManager.authenticate(username, password)) {
                        // User is returning and authenticated successfully
                        out.println("Successfully logged in.");
                        user.setLoggedIn(true);
                        user.setUsername(username);
                        user.setUserID(dbManager.getUserID(username));
                        // TODO: Set id
                    } else {
                        // User is returning but authentication failed
                        out.println("Invalid username or password.");
                    }
                } else {
                    // User is new
                    user.setUsername(username);
                    user.setPasswd(password);
                    dbManager.createUserEntry(user);
                    user.setUserID(dbManager.getUserID(username));
                    out.println("Successfully registered a new account.");
                    user.setLoggedIn(true);
                }
            }

            commands = new Commands(user);

            broadcast(user.getUsername() + " has joined the chat application!");

            while((userInput = in.readLine()) != null) {

                if(!userInput.startsWith("/")){

                    if(user.getChatRoomType() == ChatRoomType.GROUP_CHAT){
                        GroupChat currentGroupChat = user.getCurrentGroupChat();
                        currentGroupChat.broadcast(user, userInput);
                    } else if (user.getChatRoomType() == ChatRoomType.GLOBAL) {
                        broadcast(String.format("[Global] %s: %s", user.getUsername(), userInput));
                    }
                } else{
                    commands.callCommand(userInput);
                }

            }

        } catch(IOException e){
            e.printStackTrace();
            // TODO: Handle
        }
    }

}

