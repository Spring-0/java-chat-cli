package chatapplication.common.constants.util;

import chatapplication.common.models.GroupChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class IoUtil {

    private final BufferedReader in;
    private final PrintWriter out;

    public IoUtil(BufferedReader in, PrintWriter out){
        this.in = in;
        this.out = out;
    }

    // Method used to prompt user in command line
    public String prompt(String prompt) throws IOException {
        out.println(prompt);
        return in.readLine();
    }

    public static String groupChatsToString(ArrayList<GroupChat> groupChats){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < groupChats.size(); i++){
            stringBuilder.append((i + 1) + ") " + groupChats.get(i).getGroupName() + "\n");
        }
        return stringBuilder.toString();
    }


    public void displayWelcomeScreen(){
        out.println("Welcome to Spring's chat application!");
    }

    public void displayRegisterScreen(){
        out.println("Please register.");
    }
}
