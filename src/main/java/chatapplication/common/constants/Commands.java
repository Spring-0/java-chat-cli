package chatapplication.common.constants;

import chatapplication.common.constants.util.IoUtil;
import chatapplication.common.models.Chat;
import chatapplication.common.models.User;
import chatapplication.server.database.DatabaseManager;
import chatapplication.server.networking.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class Commands {

    private final User USER;
    private Chat chat;

    public Commands(User user){
        this.USER = user;
    }
    final static DatabaseManager DB_MANAGER = new DatabaseManager();

    /*
        Validate & call the appropriate command
     */
    public void callCommand(String cmd){
        String[] splitCmd = cmd.split(" ");

        switch(splitCmd[0].toLowerCase()){
            case "/nick":
                if(splitCmd.length == 2){
                    changeNick(splitCmd[1]);
                }
                break;
            case "/quit":
                quit();
                break;
            case "/dms":
                listPrivateMessages();
                break;
            case "/msg":
                if(splitCmd.length >= 3){
                    String targetUsername = splitCmd[1].toLowerCase();

                    if(ChatServer.getUsers().size() > 1) { // Verify are at least 2 users connected to the application
                        for (User u : ChatServer.getUsers()) {
                            if(!u.isLoggedIn()){continue;}

                            if (u.getUsername().toLowerCase().equals(targetUsername)) {
                                String message = String.join(" ", Arrays.copyOfRange(splitCmd, 2, splitCmd.length));
                                sendPrivateMessage(u, message);
                                return;
                            }
                        }
                    }

                    USER.getHandler().sendMessage(String.format("Unable to find the user: '%s'", targetUsername));
                }
                break;
            case "/help":
                displayHelpMenu();
                break;
            case "/passwd":
                if(splitCmd.length == 2){
                    changePasswd(splitCmd[1]);
                }
                break;
            case "/userid":
                getUserID();
                break;
            default:
                USER.getHandler().sendMessage("That is not a valid command, please refer to /help");
                break;
        }
    }

    // Change the username
    private void changeNick(String newUsername){
        final String CHANGE_NICK_MESSAGE = String.format("'%s' has changed their username to --> '%s'", USER.getUsername(), newUsername);
        DB_MANAGER.updateUsername(USER.getUsername(), newUsername);
        // Log event to server console
        System.out.println(CHANGE_NICK_MESSAGE);

        USER.getHandler().broadcast(CHANGE_NICK_MESSAGE);
        USER.setUsername(newUsername);

    }

    private void quit(){
        // TODO: Disconnect from the server, and leave application
    }

    private void sendPrivateMessage(User user, String message){
        chat = new Chat(USER, user);
        chat.sendPrivateDM(message);
    }

    private void displayHelpMenu(){
        // TODO: Create commands help menu
    }

    private void changePasswd(String newPassword){
        String newPass;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(USER.getSocket().getInputStream()));
            USER.getHandler().sendMessage("Re-enter your new password: ");
            newPass = in.readLine();
            if(newPass.equals(newPassword)){
                DB_MANAGER.updatePassword(USER.getUsername(), newPassword);
                USER.setPasswd(newPassword);
                USER.getHandler().sendMessage("You have successfully changed your password");
            } else{
                USER.getHandler().sendMessage("Passwords do not match, failed to change password.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getUserID(){
        USER.getHandler().broadcast(Integer.toString(USER.getUserID()));
    }

    private void listPrivateMessages(){
        USER.getHandler().sendMessage(USER.getOpenChats().toString());
    }


}
