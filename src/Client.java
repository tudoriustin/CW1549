import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// Client class for connecting to the server
public class Client {
    // Default IP address
    private static final String DEFAULT_IP = "localhost";

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String serverAddress = DEFAULT_IP;

        // Prompt for the server port
        System.out.print("Enter the server port: ");
        int port = scanner.nextInt();

        try {
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to server.");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // Thread for reading messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Read input from console and send to server
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                out.println(userInput);
            }

            // Close resources
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
