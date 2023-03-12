package chatapplication.common.constants;

import chatapplication.common.models.User;

public class Commands {

    private final User USER;

    public Commands(User user){
        this.USER = user;
    }

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
            case "/msg":
                if(splitCmd.length == 3){
                    // TODO: Get user by id and send them a private message
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
                System.out.println("That is not a valid command, please refer to /help");
                break;
        }
    }

    // Change the username
    private void changeNick(String newUsername){
        final String CHANGE_NICK_MESSAGE = String.format("'%s' has changed their username to --> '%s'", USER.getUsername(), newUsername);

        // Log event to server console
        System.out.println(CHANGE_NICK_MESSAGE);

        USER.getHandler().broadcast(CHANGE_NICK_MESSAGE);
        USER.setUsername(newUsername);
    }

    private void quit(){
        // TODO: Disconnect from the server, and leave application
    }

    private void sendPrivateMessage(User user, String message){
        user.privateMessage(message);
    }

    private void displayHelpMenu(){
        // TODO: Create commands help
    }

    private void changePasswd(String newPassword){
        USER.setPasswd(newPassword);
    }

    private void getUserID(){
        USER.getHandler().broadcast(Integer.toString(USER.getUserID()));
    }

}
