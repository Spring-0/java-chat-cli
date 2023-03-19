package chatapplication.common.constants;

import chatapplication.common.constants.util.IoUtil;
import chatapplication.common.models.Chat;
import chatapplication.common.models.GroupChat;
import chatapplication.common.models.User;
import chatapplication.server.ChatRoomType;
import chatapplication.server.database.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class Commands {

    private User USER;

    public Commands(User user){
        this.USER = user;
    }

    final static DatabaseManager DB_MANAGER = new DatabaseManager();


    public void setUser(User user){
        this.USER = user;
        System.out.println("USERNAME: " + USER.getUsername() + " " + USER.getGroupChats());
    }

    /*
        Validate & call the appropriate command
     */
    public void callCommand(String cmd){
        GroupChat currentGroupChat;
        String[] splitCmd = cmd.split(" ");

        switch(splitCmd[0].toLowerCase()){
            // Change the user's username
            case "/nick":
                if(splitCmd.length == 2){
                    changeNick(splitCmd[1]);
                }
                break;

            // TODO: Elegantly close the application
            case "/quit":
                quit();
                break;

            // List group chats the sender is in
            case "/groups":
                listGroupChats();
                break;

            // Privately message specific user
            case "/msg":
                if(splitCmd.length >= 3){
                    String targetUsername = splitCmd[1].toLowerCase();

                    if(targetUsername.equals(USER.getUsername())){
                        USER.getHandler().sendMessage("You cannot send a message to yourself!");
                        return;
                    }

                    User targetUser = User.getUserByUsername(targetUsername);
                    String message;

                    if(targetUser != null){
                        message = String.join(" ", Arrays.copyOfRange(splitCmd, 2, splitCmd.length));
                        sendPrivateMessage(targetUser, message);
                    } else{
                        USER.getHandler().sendMessage(String.format("Unable to find the user: '%s'", targetUsername));
                    }
                }
                break;

            // Display help menu
            case "/help":
                displayHelpMenu();
                break;

            // Change user password
            case "/passwd":
                if(splitCmd.length == 2){
                    changePasswd(splitCmd[1]);
                }
                break;

            case "/myid":
                getUserID();
                break;


            case "/groupchat":

                if(splitCmd.length >= 2){

                    // Add a new user to the groupchat
                    if(splitCmd[1].equalsIgnoreCase("add")){

                        // TODO: Handle invalid inputs
                        ArrayList<GroupChat> groupChats = USER.getGroupChats();
                        if(groupChats.size() == 0){
                            USER.getHandler().sendMessage("You do not have any group chats.");
                        } else{
                            User addedUser = User.getUserByUsername(splitCmd[2]);
                            if(addedUser == null){
                                USER.getHandler().sendMessage("This user is not online or invalid.");
                                break;
                            }
                            USER.getHandler().sendMessage("Select a group chat to add the user to.\n" + IoUtil.groupChatsToString(groupChats));
                            int groupChatIndex = Integer.parseInt(USER.getHandler().getUserInput());
                            GroupChat gc = groupChats.get(groupChatIndex - 1);
                            System.out.println("Name: " + gc.getGroupName() + "\nID: " + gc.getGroupID());
                            gc.addUserToGroup(addedUser);
                            DB_MANAGER.addUserToGroup(addedUser, gc);
                            addedUser.getHandler().sendMessage("You have been added to the " + gc.getGroupName() + " group chat.");
                            USER.getHandler().sendMessage("You have successfully added " + addedUser.getUsername() + " to the group chat.");
                        }

                    // Create new group chat
                    } else if(splitCmd[1].equalsIgnoreCase("create")){
                        String groupName;
                        if(splitCmd.length > 2){
                            groupName = String.join(" ", Arrays.copyOfRange(splitCmd, 2, splitCmd.length));
                        } else{
                            USER.getHandler().sendMessage("Please enter a name for the groupchat: ");
                            groupName = USER.getHandler().getUserInput();
                        }
                        createNewGroupChat(Arrays.copyOfRange(splitCmd, 1, splitCmd.length), groupName);
                    }

                // Switch to group chat
                } else if (splitCmd.length == 1) {
                    if(USER.getGroupChats().size() > 0){
                        USER.getHandler().sendMessage("Which groupchat would you like to switch into?\n" + IoUtil.groupChatsToString(USER.getGroupChats()));
                        int groupChatIndex = Integer.parseInt(USER.getHandler().getUserInput());
                        USER.setCurrentGroupChat(USER.getGroupChats().get(groupChatIndex-1));
                        USER.setChatRoomType(ChatRoomType.GROUP_CHAT);
                        USER.getHandler().sendMessage("You have switched to the group chat.");
                    } else{
                        USER.getHandler().sendMessage("You are not in any group chats.");
                    }
                }
                break;

            // Kick a user from the current group chat
            case "/kick":
                if(splitCmd.length == 2){
                    currentGroupChat = USER.getCurrentGroupChat();
                    User kickedUser = User.getUserByUsername(splitCmd[1]);
                    if(kickedUser.getGroupChats().contains(currentGroupChat)){
                        currentGroupChat.kickUser(kickedUser);
                        USER.getHandler().sendMessage(String.format("You have kicked '%s' from this group chat.", kickedUser.getUsername()));
                    } else{
                        USER.getHandler().sendMessage(String.format("'%s' is not in this group chat.", kickedUser.getUsername()));
                    }
                }
                break;

            // Rename current group chat
            case "/rename":
                String groupChatNameInput;
                if(splitCmd.length == 1){
                    USER.getHandler().sendMessage("What would you like to rename this group chat to?");
                    groupChatNameInput = USER.getHandler().getUserInput();
                } else{
                    groupChatNameInput = String.join(" ", Arrays.copyOfRange(splitCmd, 1, splitCmd.length)); // Join elements 1.. into a string
                }
                currentGroupChat = USER.getCurrentGroupChat(); // get the current group chat
                USER.getHandler().sendMessage(String.format("You have successfully renamed '%s' --> '%s'", currentGroupChat.getGroupName(), groupChatNameInput));
                currentGroupChat.setGroupName(groupChatNameInput); // Update the name
                break;


            // Switch to global chat
            case "/global":
                USER.setChatRoomType(ChatRoomType.GLOBAL);
                USER.getHandler().sendMessage("You have switched to global chat.");
                break;


            default:
                USER.getHandler().sendMessage("That is not a valid command, please refer to /help");
                break;

        }
    }

    // Change the username
    // TODO: Prevent duplicate nicknames
    private void changeNick(String newUsername){
        final String CHANGE_NICK_MESSAGE = String.format("'%s' has changed their username to --> '%s'", USER.getUsername(), newUsername);

        DB_MANAGER.updateUsername(USER.getUsername(), newUsername);

        // Log event to server console
        System.out.println(CHANGE_NICK_MESSAGE);

        USER.getHandler().broadcast(CHANGE_NICK_MESSAGE);
        USER.changeUsername(newUsername);

    }

    private void quit(){
        // TODO: Disconnect from the server, and leave application
    }

    private void sendPrivateMessage(User user, String message){
        Chat chat = new Chat(USER, user);
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

    private void createNewGroupChat(String[] splitCmd, String groupName){
        ArrayList<User> receivers = new ArrayList<>();
        receivers.add(USER);

        if(splitCmd.length > 2){
            for(int i = 1; i < splitCmd.length; i++){
                User user = User.getUserByUsername(splitCmd[i]);
                if(user == null){
                    USER.getHandler().sendMessage("Unable to find the user: " + splitCmd[i]);
                    continue;
                }
                receivers.add(user);
            }

        }

        GroupChat groupChat = new GroupChat(receivers);
        groupChat.setGroupName(groupName);
        DB_MANAGER.addGroupChat(groupChat);
        USER.getHandler().sendMessage("Successfully created a new groupchat.");

    }

    private void getUserID(){
        USER.getHandler().broadcast(Integer.toString(USER.getUserID()));
    }

    private void listGroupChats(){
        USER.getHandler().sendMessage(USER.getGroupChats().toString());
    }


}
