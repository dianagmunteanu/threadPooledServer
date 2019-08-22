package server;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log
public class NotHttpRequestIT extends BaseIT {

    @Test
    public void testSocket() throws IOException {
        Socket socket = new Socket(host, port);

        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        pw.println("KO request");
        pw.println("");

        Scanner scanner = new Scanner(socket.getInputStream());
        assertEquals("Not Supported", scanner.nextLine());
        socket.close();
    }

}
