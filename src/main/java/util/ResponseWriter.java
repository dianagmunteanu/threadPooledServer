package util;

import http.HttpRequest;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

@Log
public class ResponseWriter {

    public void httpVersionNotSupported(OutputStream output, String protocolVersion) {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
        pw.println(protocolVersion + " 505 HTTP Version Not Supported");
        pw.println("Connection: close");
        pw.println("");
        pw.println("<h1> 505 HTTP Version Not Supported </h1>");
        pw.println("");
        pw.flush();
    }

    public void reject(OutputStream outputStream) {
        reject(outputStream, "400 Bad request");
    }

    public void reject(OutputStream outputStream, String message) {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
        pw.println("HTTP/1.1 " + message);
        pw.println("Connection: close");
        pw.println("");
        pw.println("<h1> " + message + " </h1>");
        pw.println("");
        pw.flush();
    }

    public void sendResponse(OutputStream output, HttpRequest httpRequest) {
        sendResponse(output, httpRequest, "");
    }

    public void sendResponse(OutputStream output, HttpRequest httpRequest, String message) {
        if (message == null) {
            sendResponse(output, httpRequest);
        }
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
        pw.println(httpRequest.getRequestLine().getProtocolVersion() + " 200 OK");
        pw.println(String.format("Content-Length: %d", message.length()));
        if (httpRequest.keepAlive()) {
            pw.println("Connection: keep-alive");
        } else {
            pw.println("Connection: close");
        }
        pw.println("");
        if (!"".equals(message)) {
            pw.println(message);
            pw.println("");
        }
        pw.flush();
    }


    public void serviceUnavailable(Socket clientSocket) {
        try {
            reject(clientSocket.getOutputStream(), "503 Service Unavailable");
        } catch (IOException e) {
            log.log(Level.WARNING, "Could not send Service Unavailable", e);
        }

    }
}
