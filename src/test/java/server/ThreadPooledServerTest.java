package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThreadPooledServerTest {

    @Test
    public void shouldThrowRuntimeExceptionWhenStartingServerOnAUsedPort() {
        ThreadPooledServer server = new ThreadPooledServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> new ThreadPooledServer().run());
        assertEquals("Could not start server!", ex.getMessage());

        serverThread.interrupt();
    }
}