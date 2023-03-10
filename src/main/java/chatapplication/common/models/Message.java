package chatapplication.common.models;

public class Message {

    private int messageId;
    private String message;
    private User user;

    public Message(){
        this.messageId = 0;
    }


    private boolean messageExists(){
        // TODO: Return bool on message existance
        return false;
    }

    private void delete(){
        // TODO: Delete the message
    }

    public User getAuthor(){
        return user;
    }



}
