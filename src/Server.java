import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

// Server class for handling client connections
public class Server {
    // Default IP address
    private static final String DEFAULT_IP = "localhost";

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask for the port number for the server
        System.out.print("Enter the port number: ");
        int port = scanner.nextInt();

        try {
            // Creating the server socket with the specified port number
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on " + DEFAULT_IP + ":" + port);

            // Listen for incoming connections from users in the server network
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New member connected: " + clientSocket);

                // Start a new thread to handle client communication
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
