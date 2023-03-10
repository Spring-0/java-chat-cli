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
            throw new RuntimeException(e);
        }
    }
    class InputHandler implements Runnable{

        @Override
        public void run() {
            Scanner inScanner = new Scanner(System.in);
            while(!done){
                sendMessage(inScanner.nextLine());
            }
        }
    }


    public static void main(String[] args) {
        Client client = new Client();
        Thread t = new Thread(client);
        t.start();
    }
}
