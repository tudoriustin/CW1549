import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.*;

public class ServerTest {

    @Test
    void testServerStart() {
        // Simulate user input
        String input = "1122";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Run your main method in a separate thread
        Thread serverThread = new Thread(() -> {
            try {
                // Start the server on localhost
                Server.main(null); // No arguments needed
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Wait for the server to start (adjust sleep time as needed)
        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the output contains the expected message
        String expectedOutput = "Server started on localhost:1122";
        String actualOutput = outputStream.toString().trim();
        assertTrue(actualOutput.contains(expectedOutput));

        // Reset System.in and System.out
        System.setIn(System.in);
        System.setOut(originalOut);
    }
}
