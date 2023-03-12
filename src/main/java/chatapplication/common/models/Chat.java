package chatapplication.common.models;

import chatapplication.common.models.User;

import java.util.Date;

public class Chat {

    private int[] userMembers;
    private boolean expireMessages = false;
    private String chatName = "";
    private Date creationDate;

    public Chat(){}


    public Chat(int[] userMembers){
        User user = new User();

        this.userMembers = userMembers;

        // Set the chat name to the list of usernames
        for(int userID : this.userMembers){
            this.chatName +=  user.getUsernameFromID(1) + ", ";
        }

        // Create timestamp of creation date
        // TODO: Implement timestamp

    }


    // Update the chat name
    public void setChatName(String chatName){
        this.chatName = chatName;
    }

    // Will delete messages from a chat after a set time if this is true
    public void setExpireMessages(boolean expireMessages){
        this.expireMessages = expireMessages;
    }


}
