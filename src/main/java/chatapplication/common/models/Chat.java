package chatapplication.common.models;

public class Chat {

    private User sender;
    private User receiver;


    public Chat(){}

    public Chat(User sender, User receiver){
        this.sender = sender;
        this.receiver = receiver;
    }


    public void sendPrivateDM(String message){
        this.sender.getHandler().sendMessage(String.format("[Private Message] %s --> %s: %s", sender.getUsername(), receiver.getUsername(), message));
        this.receiver.getHandler().sendMessage(String.format("[Private Message] %s --> %s: %s", sender.getUsername(), receiver.getUsername(), message));
    }

}
