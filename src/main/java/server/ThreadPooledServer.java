package server;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import util.ResponseWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;

import static config.Config.*;

@Log
@NoArgsConstructor
public class ThreadPooledServer implements Runnable {


    private int serverPort = defaultServerPort;
    private boolean isStopped = false;
    private int corePoolSize = defaultCorePoolSize;
    private int maxPoolSize = defaultMaxPoolSize;
    private int keepAliveTime = defaultKeepAliveTimeInSeconds;
    private BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
    private ServerSocket serverSocket = null;
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private Thread runningThread = null;

    public ThreadPooledServer(int port) {
        this.serverPort = port;
    }

    public void run() {
        openServerSocket();
        while (!isStopped) {
            acceptRequest();
        }
        stopServer();
    }

    public synchronized void stop() {
        isStopped = true;
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            log.log(Level.SEVERE, String.format("Cannot open port %d", serverPort), e);
            throw new RuntimeException("Could not start server!");
        }
    }

    private void acceptRequest() {
        Socket clientSocket = null;
        try {
            clientSocket = this.serverSocket.accept();
            log.info("Accepted:" + clientSocket);
            this.pool.execute(new WorkerRunnable(clientSocket));

        } catch (RejectedExecutionException e) {
            log.log(Level.WARNING, "No workers are available.", e);
            new ResponseWriter().serviceUnavailable(clientSocket);
            closeClientSocket(clientSocket);
        } catch (IOException e) {
            if (isStopped) {
                log.log(Level.SEVERE, "Server Stopped.", e);
                //TODO clean and restart?
            }
        }
    }

    private void stopServer() {
        this.pool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Closing server failed", e);
        }
        log.info("Server Stopped.");
    }

    private void closeClientSocket(Socket clientSocket) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Closing client socket failed", e);
        }
    }
}