package chatapplication.server.networking;

import chatapplication.common.constants.Commands;
import chatapplication.common.constants.util.IoUtil;
import chatapplication.common.models.User;
import chatapplication.server.UserAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class RequestHandler implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private User user;

    public RequestHandler(){
    }

    public void setUser(User user){
        this.user = user;
    }

    public void sendMessage(String msg){
        out.println(msg);
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
        UserAuth auth = new UserAuth();
        IoUtil ioUtil;

        try{
            in = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
            out = new PrintWriter(user.getSocket().getOutputStream(), true);

            ioUtil = new IoUtil(in, out);

            ioUtil.displayWelcomeScreen();
            // Prompt user for username and password
            username = ioUtil.prompt("Enter a username: ");
            password = ioUtil.prompt("Enter a password: ");

            user.setUsername(username);
            user.setPasswd(password);
            user.setLoggedIn(true);

            commands = new Commands(user);

            broadcast(user.getUsername() + " has joined the chat application!");

            while((userInput = in.readLine()) != null) {
                if(!userInput.startsWith("/")){
                    broadcast(String.format("%s: %s", user.getUsername(), userInput));
                } else{
                    commands.callCommand(userInput);
                }
            }

        } catch(IOException e){
            e.printStackTrace();
            // TODO: Handle
        }
    }

}

