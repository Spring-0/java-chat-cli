package chatapplication.common.models;

import chatapplication.server.ChatRoomType;
import chatapplication.server.networking.RequestHandler;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private int userID;
    private String username;
    private String password;
    private ChatRoomType chatRoomType = ChatRoomType.GLOBAL;
    private RequestHandler handler;
    private Socket socket;
    private boolean loggedIn;
    private GroupChat currentGroupChat;
    private ArrayList<GroupChat> groupChats;

    // Map username to User object
    private static Map<String, User> userMap = new HashMap<>();

    public User(){
        groupChats = new ArrayList<>();
    }

    public void setHandler(RequestHandler handler){
        this.handler = handler;
    }

    public void setUsername(String username){
        this.username = username;
        userMap.put(username.toLowerCase(), this);
    }

    public void changeUsername(String username){
        // Remove old mapping
        userMap.remove(this.username);

        // Set new username and add mapping
        setUsername(username);
    }

    public void setPasswd(String passwd){
        this.password = passwd;
    }

    public String getPasswd(){
        return this.password;
    }


    public RequestHandler getHandler(){
        return this.handler;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public void setSocket(Socket socket){
        this.socket = socket;
    }

    public void setCurrentGroupChat(GroupChat currentGroupChat){
        this.currentGroupChat = currentGroupChat;
    }

    public GroupChat getCurrentGroupChat(){
        return this.currentGroupChat;
    }

    public String[] getFriendList(){
        // TODO: Return array of friend list
        return new String[1];
    }

    public String getUsername(){
        return this.username;
    }

    public void setChatRoomType(ChatRoomType chatRoomType){
        this.chatRoomType = chatRoomType;
    }
    public ChatRoomType getChatRoomType(){
        return this.chatRoomType;
    }

    public void setLoggedIn(boolean bool){
        this.loggedIn = bool;
    }

    public Boolean isLoggedIn(){
        return this.loggedIn;
    }

    public int getUserID(){
        return this.userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    public ArrayList<GroupChat> getGroupChats(){
        return groupChats;
    }
    public void setGroupChats(ArrayList<GroupChat> groupChats){
        this.groupChats = groupChats;
    }


    public static User getUserByUsername(String username){
        return userMap.get(username.toLowerCase());
    }

}
