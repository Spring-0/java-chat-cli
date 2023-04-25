package chatapplication.server.networking;

import chatapplication.common.Commands;
import chatapplication.util.IoUtil;
import chatapplication.common.models.GroupChat;
import chatapplication.common.models.User;
import chatapplication.common.constants.ChatRoomType;
import chatapplication.server.database.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;

/*
 * Handles client connections,
 * responsible for user IO
 */
public class RequestHandler implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private final User USER;

    public RequestHandler(User user){
        this.USER = user;
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
            in = new BufferedReader(new InputStreamReader(USER.getSocket().getInputStream()));
            out = new PrintWriter(USER.getSocket().getOutputStream(), true);
            ioUtil = new IoUtil(in, out);

            // Display welcome screen
            ioUtil.displayWelcomeScreen();

            while(!USER.isLoggedIn()){
                // Prompt USER for username and password
                username = ioUtil.prompt("Enter username: ");
                password = ioUtil.prompt("Enter password: ");


                if (dbManager.usernameExists(username)) {
                    if (dbManager.authenticate(username, password)) {
                        // User is returning and authenticated successfully
                        out.println("Successfully logged in.");
                        USER.setLoggedIn(true);
                        USER.setUsername(username);
                        USER.setUserID(dbManager.getUserID(username));

                        ArrayList<GroupChat> groupChats = dbManager.getGroupChatsByUser(USER);
                        USER.setGroupChats(groupChats);


                        // Debug Purposes
                        System.out.println(USER.getGroupChats().toString());

                    } else {
                        // User is returning but authentication failed
                        out.println("Invalid username or password.");
                    }
                } else {
                    // User is new
                    USER.setUsername(username);
                    USER.setPasswd(password);
                    dbManager.createUserEntry(USER);
                    USER.setUserID(dbManager.getUserID(username));
                    out.println("Successfully registered a new account.");
                    USER.setLoggedIn(true);
                }
            }

            commands = new Commands(USER);

            broadcast(USER.getUsername() + " has joined the chat application!");

            try {
                while ((userInput = in.readLine()) != null) {

                    if (!userInput.startsWith("/")) {

                        if (USER.getChatRoomType() == ChatRoomType.GROUP_CHAT) {
                            GroupChat currentGroupChat = USER.getCurrentGroupChat();
                            currentGroupChat.broadcast(USER, userInput);
                        } else if (USER.getChatRoomType() == ChatRoomType.GLOBAL) {
                            broadcast(String.format("[Global] %s: %s", USER.getUsername(), userInput));
                        }
                    } else {
                        commands.callCommand(userInput);
                    }

                }
            } catch (SocketException e){

            }

        } catch(IOException e){
            e.printStackTrace();
            // TODO: Handle
        }
    }

}

