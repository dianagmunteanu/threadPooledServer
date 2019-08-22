package server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class BaseIT {
    protected static int port = 9000;
    private static ThreadPooledServer server;
    protected String host = "localhost";
    protected final String url = String.format("http://%s:%d/", host, port);

    @BeforeAll
    public static void startServer() {
        server = new ThreadPooledServer(port);
        new Thread(server).start();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }
}
