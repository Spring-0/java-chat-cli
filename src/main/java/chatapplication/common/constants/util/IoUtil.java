package chatapplication.common.constants.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class IoUtil {

    private BufferedReader in;
    private PrintWriter out;

    public IoUtil(BufferedReader in, PrintWriter out){
        this.in = in;
        this.out = out;
    }

    // Method used to prompt user in command line
    public String prompt(String prompt) throws IOException {
        out.println(prompt);
        return in.readLine();
    }
    public void displayWelcomeScreen(){
        out.println("Welcome to Spring's chat application!");
    }

    public void displayRegisterScreen(){
        out.println("Please register.");
    }
}
