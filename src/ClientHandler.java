import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//This class will handle the connections from the client to the main server, in simple I called it CLientHandler
public class ClientHandler implements Runnable {
    // Define the network member variables below
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ClientHandler coordinator;

    // Constructor
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    // Constructor with BufferedReader and PrintWriter parameters for testing
    public ClientHandler(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    // Setter for input stream
    public void setInputStream(BufferedReader in) {
        this.in = in;
    }

    // Setter for output stream
    public void setOutputStream(PrintWriter out) {
        this.out = out;
    }

    // Override and write the function for the thread execution
    @Override
    public void run() {
        try {
            // Setting up input and output streams for communication between client and the main server app
            if (in == null) {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }
            if (out == null) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            }

            // Asks for the client's name
            out.println("Name:");
            clientName = in.readLine();

            if (clients.isEmpty()) {
                coordinator = this;
                out.println("You are the coordinator."); // when the first person joins the server they will be assigned the coordinator sole and be told so
            } else {
                out.println(coordinator.clientName + " is the coordinator");
            }

            // Add client to the list of the active members
            clients.add(this);

            // Read input from the client
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(clientName + ": " + inputLine);
                processInput(inputLine);
            }

            // Network member disconnects
            System.out.println("Client disconnected: " + clientName); //prints out a message with the name of the member who disconnected
            clients.remove(this);
            if (this == coordinator) {
                if (!clients.isEmpty()) {
                    coordinator = clients.get(0);
                    coordinator.sendMessage("You're assigned as the new coordinator."); //when the main coordinator leaves, the next person who joined is the new one
                } else {
                    coordinator = null;
                }
            }
            clientSocket.close(); // closing client socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to help with the processing of different inputs from the members as well as custom commands
    private void processInput(String inputLine) {
        // MEmber inputs will be processed in this part of the code
        if (inputLine.startsWith("/broadcast")) { // the broadcast command allows everyone to send out a public message to all the clients in the server
            String message = inputLine.substring("/broadcast".length()).trim();
            broadcastMessage(clientName + ": " + message);
        } else if (inputLine.startsWith("/private")) { // the private command allows everyone to send out a private message to a specific user in the network
            String[] parts = inputLine.split(" ", 3);
            if (parts.length == 3) {
                String recipient = parts[1];
                String message = parts[2];
                sendPrivateMessage(recipient, message);
            } else {
                sendMessage("Try again. /private user message"); //if someone types the command wrong, the server will give them the correct layout
            }
        } else if (inputLine.equals("/help")) { //if someone doesn't know the commands available, the help command will output them all at once so they can choose from it
            sendMessage("Commands /broadcast , /private , /help");
            if (this == coordinator) {
                sendMessage("Coordinator commands: /kick user"); //this is the kick command which allows the coordinator to kick out a specific user from the server
            }
        } else if (inputLine.startsWith("/kick")) {
            if (this == coordinator) {
                String[] parts = inputLine.split(" ", 2);
                if (parts.length == 2) {
                    String clientName = parts[1];
                    kickClient(clientName);
                } else {
                    sendMessage("Try again, Format: /kick name");
                }
            } else {
                sendMessage("Only the coordinator can use the /kick command."); // if someone else tries to kick someone out, this message will be displayed
            }
        } else {
            sendMessage("Unknown command. Type /help to see available commands.");
        }
    }

    // Function that allows users to broadcast a message to all server clients
    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    // Function that allows users to broadcast a message to all server clients
    private void sendPrivateMessage(String receiverName, String message) {
        for (ClientHandler client : clients) {
            if (client.clientName.equals(receiverName)) {
                client.sendMessage("Message from " + clientName + " : " + message);
                return;
            }
        }
        sendMessage("User not found.");
    }

    // Function to kick a client out of the chat
    private void kickClient(String clientName) {
        for (ClientHandler client : clients) {
            if (client.clientName.equals(clientName)) {
                client.sendMessage("You have been kicked out by the coordinator.");
                clients.remove(client);
                client.closeSocket();
                return;
            }
        }
        sendMessage("User not found.");
    }

    // Function that sends message to a client
    private void sendMessage(String message) {
        out.println(message);
    }

    // Function to close the client's socket
    private void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
