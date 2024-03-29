import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.*;

public class ClientTest {

    @Test
    void testClientConnection() {
        // Simulate user input by reading from console
        String input = "1122"; // Port number is my birthday for testing purposes
        InputStream originalIn = System.in;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Run your main method
        Thread clientThread = new Thread(() -> {
            try {
                Client.main(null); // No arguments needed
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        clientThread.start();

        // Wait for the client to connect (adjust sleep time as needed)
        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the output contains the expected message
        String expectedOutput = "Connected to server.";
        String actualOutput = outputStream.toString().trim();
        assertEquals(expectedOutput, actualOutput);

        // Reset System.in and System.out
        System.setIn(originalIn);
        System.setOut(originalOut);
    }
}
