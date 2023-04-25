package chatapplication.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    private Boolean done = false;
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;


    public void sendMessage(String msg){
        out.println(msg);
    }

    @Override
    public void run() {
        try {

            client = new Socket("localhost", 8888);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            InputHandler inHandler = new InputHandler();
            new Thread(inHandler).start();

            String inMessage;
            while((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            System.out.println("There was an error connecting to the server, please ensure the server is online.");
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            Scanner inScanner = new Scanner(System.in);
            while (!done) {
                String userInput = inScanner.nextLine();
                sendMessage(userInput);
                if (userInput.equalsIgnoreCase("/quit")) {
                    done = true;
                    closeResources();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        Thread t = new Thread(client);
        t.start();
    }
}