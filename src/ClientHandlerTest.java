import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.*;

public class ClientHandlerTest {

    @Test
    public void testClientHandler() throws IOException {
        // Mock client input and output streams
        String clientInput = "Test message\n";
        BufferedReader reader = new BufferedReader(new StringReader(clientInput));
        StringWriter writer = new StringWriter();

        // Create ClientHandler instance
        ClientHandler clientHandler = new ClientHandler(new BufferedReader(new StringReader("")), new PrintWriter(writer));

        // Run the client handler
        Thread handlerThread = new Thread(clientHandler);
        handlerThread.start();

        // Wait for the handler to process input
        try {
            Thread.sleep(100); // Adjust sleep time as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check the output
        String expectedOutput = "Name:";
        assertEquals(expectedOutput, writer.toString().trim());
    }
}
