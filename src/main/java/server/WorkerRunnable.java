package server;

import http.HttpRequest;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import util.ResponseWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;


@AllArgsConstructor
@Log
public class WorkerRunnable implements Runnable {

    private Socket clientSocket;

    public void run() {
        ResponseWriter responseWriter = new ResponseWriter();
        HttpRequest httpRequest = null;
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {
            do {
                if (input.available() > 0) {
                    httpRequest = new HttpRequest(input);
                    if (httpRequest.getRequestLine() == null) {
                        responseWriter.reject(clientSocket.getOutputStream());
                        break;
                    }
                    String httpVersion = httpRequest.getRequestLine().getProtocolVersion().toString();
                    if (!httpVersion.equals("HTTP/1.0") && !httpVersion.equals("HTTP/1.1")) {
                        responseWriter.httpVersionNotSupported(clientSocket.getOutputStream(), "Protocol version not suported!");
                        break;
                    }
                    readAllContent(httpRequest);
                    clientSocket.setKeepAlive(httpRequest.keepAlive());
                    responseWriter.sendResponse(output, httpRequest);
                }
            } while (clientSocket.getKeepAlive() && !clientSocket.isClosed() || httpRequest == null);

        } catch (IOException e) {
            //TODO check if client socket timed out
            log.log(Level.SEVERE, "Request processing failed", e);

        } finally {
            closeSocket();
        }
    }

    private void readAllContent(HttpRequest httpRequest) {
        //TODO add proper request content handling
        log.info("Reading message content");
        int contentLengthLocal = httpRequest.contentLength();
        if (contentLengthLocal <= 0)
            return;

        int chunk = 5000;
        byte[] contentBuffer = new byte[chunk];
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            while (contentLengthLocal > 0) {
                int result = httpRequest.getInputStream().read(contentBuffer, 0, chunk);
                if (result > 0) {
                    byteStream.write(contentBuffer, 0, result);
                    contentLengthLocal -= result;
                }
                if (result == -1) {
                    break;
                }
            }
            byte[] content = byteStream.toByteArray();
            String s = new String(content);
            log.info(s);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not extract content.", e);
        }
    }

    public void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Socket close has failed", e);
        }
    }
}