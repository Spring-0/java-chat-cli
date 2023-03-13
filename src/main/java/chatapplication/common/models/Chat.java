package chatapplication.common.models;

import chatapplication.common.models.User;

import java.util.Date;

public class Chat {

    private User sender;
    private User reciever;
    private boolean expireMessages = false;
    private String chatName = "";


    public Chat(User sender, User reciever){
        this.sender = sender;
        this.reciever = reciever;
    }


    public void sendPrivateDM(String message){
        this.sender.getHandler().sendMessage(String.format("[Private Message] %s --> %s: %s", sender.getUsername(), reciever.getUsername(), message));
        this.reciever.getHandler().sendMessage(String.format("[Private Message] %s --> %s: %s", sender.getUsername(), reciever.getUsername(), message));
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
