package chatapplication.common.models;

import chatapplication.server.database.DatabaseManager;
import chatapplication.server.networking.RequestHandler;

import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;

public class User {

    private int userID;
    private String username;
    private String email;
    private ArrayList<String> chatHistory;
    private String password;
    private Inet4Address ipAddress;
    private RequestHandler handler;
    private Socket socket;
    private boolean loggedIn;

    public User(){
    }

    public User(String username, String passwd){
        this.username = username;
        this.password = passwd;
    }


    public void privateMessage(String message){
        // TODO: Send a message
    }


    public void setHandler(RequestHandler handler){
        this.handler = handler;
    }

    public void setUsername(String username){
        this.username = username;
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

    public String getUsernameFromID(int userID){
        return "";
    }


    // Set or update the user ip
    public void setIpAddress(Inet4Address ip){
        this.ipAddress = ip;
    }


    public String[] getFriendList(){
        // TODO: Return array of friend list
        return new String[1];
    }

    public String getUsername(){
        return this.username;
    }

    public ArrayList<String> getChatHistory(){
        return chatHistory;
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


}
