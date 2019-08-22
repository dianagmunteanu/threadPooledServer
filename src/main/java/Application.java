import lombok.extern.java.Log;
import server.ThreadPooledServer;

import java.util.logging.Level;

@Log
public class Application {

    public static void main(String[] args) {
        ThreadPooledServer server = new ThreadPooledServer();
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Server error", e);
        }

        log.info("Stopping Server");
        server.stop();
    }
}
