package http;

import com.sun.net.httpserver.Headers;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;

import static config.Config.maxReqHeaders;

@Log
public class HttpRequest {

    @Getter
    private RequestLine requestLine;

    @Getter
    private InputStream inputStream;

    private char[] buf = new char[2048];
    private int pos;
    private StringBuffer lineBuf;
    private Headers headers = null;


    public HttpRequest(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        String startLine;
        do {
            startLine = this.readLine();
            if (startLine == null) {
                return;
            }
        } while (startLine != null && startLine.equals(""));
        try {
            requestLine = parseRequestLine(startLine);
        } catch (IllegalArgumentException ex) {
            return;
        }
        readHeaders();

    }

    /*
    from sun.net.httpserver.Request
    */
    private String readLine() throws IOException {
        boolean var1 = false;
        boolean var2 = false;
        this.pos = 0;
        this.lineBuf = new StringBuffer();

        while (!var2) {
            int var3 = this.inputStream.read();
            if (var3 == -1) {
                return null;
            }

            if (var1) {
                if (var3 == 10) {
                    var2 = true;
                } else {
                    var1 = false;
                    this.consume(13);
                    this.consume(var3);
                }
            } else if (var3 == 13) {
                var1 = true;
            } else {
                this.consume(var3);
            }
        }
        this.lineBuf.append(this.buf, 0, this.pos);
        return new String(this.lineBuf);
    }

    /*
     from sun.net.httpserver.Request
    */
    private void consume(int var1) {
        if (this.pos == 2048) {
            this.lineBuf.append(this.buf);
            this.pos = 0;
        }

        this.buf[this.pos++] = (char) var1;
    }

    /*
     from sun.net.httpserver.Request
   */
    private void readHeaders() throws IOException {
        if (this.headers != null) {
            return;
        } else {
            this.headers = new Headers();
            char[] var1 = new char[10];
            byte var2 = 0;
            int var3 = this.inputStream.read();
            int var4;
            if (var3 == 13 || var3 == 10) {
                var4 = this.inputStream.read();
                if (var4 == 13 || var4 == 10) {
                    return;
                }

                var1[0] = (char) var3;
                var2 = 1;
                var3 = var4;
            }

            while (var3 != 10 && var3 != 13 && var3 >= 0) {
                var4 = -1;
                boolean var6 = var3 > 32;
                int var9 = var2 + 1;
                var1[var2] = (char) var3;

                label112:
                while (true) {
                    int var5;
                    if ((var5 = this.inputStream.read()) < 0) {
                        var3 = -1;
                        break;
                    }

                    switch (var5) {
                        case 9:
                            var5 = 32;
                        case 32:
                            var6 = false;
                            break;
                        case 10:
                        case 13:
                            var3 = this.inputStream.read();
                            if (var5 == 13 && var3 == 10) {
                                var3 = this.inputStream.read();
                                if (var3 == 13) {
                                    var3 = this.inputStream.read();
                                }
                            }

                            if (var3 == 10 || var3 == 13 || var3 > 32) {
                                break label112;
                            }

                            var5 = 32;
                            break;
                        case 58:
                            if (var6 && var9 > 0) {
                                var4 = var9;
                            }

                            var6 = false;
                    }

                    if (var9 >= var1.length) {
                        char[] var7 = new char[var1.length * 2];
                        System.arraycopy(var1, 0, var7, 0, var9);
                        var1 = var7;
                    }

                    var1[var9++] = (char) var5;
                }

                while (var9 > 0 && var1[var9 - 1] <= ' ') {
                    --var9;
                }

                String var10;
                if (var4 <= 0) {
                    var10 = null;
                    var4 = 0;
                } else {
                    var10 = String.copyValueOf(var1, 0, var4);
                    if (var4 < var9 && var1[var4] == ':') {
                        ++var4;
                    }

                    while (var4 < var9 && var1[var4] <= ' ') {
                        ++var4;
                    }
                }

                String var8;
                if (var4 >= var9) {
                    var8 = new String();
                } else {
                    var8 = String.copyValueOf(var1, var4, var9 - var4);
                }

                if (this.headers.size() >= maxReqHeaders) {
                    throw new IOException("Maximum number of request readHeaders (sun.net.httpserver.maxReqHeaders) exceeded, " + maxReqHeaders + ".");
                }

                this.headers.add(var10, var8);
                var2 = 0;
            }

            return;
        }
    }

    private RequestLine parseRequestLine(String startLine) {
        Method method;
        String uri;
        ProtocolVersion ver;
        String[] splitStartLine = startLine.split(" ");
        if (splitStartLine.length != 3) {
            throw new IllegalArgumentException("invalid first line in the request");
        }
        method = Method.valueOf(splitStartLine[0]);
        uri = splitStartLine[1];
        ver = new ProtocolVersion(splitStartLine[2]);

        return new RequestLine(method, uri, ver);
    }


    public boolean keepAlive() {
        if (headers != null && headers.get("Connection") != null) {
            return headers.get("Connection").stream().anyMatch(s -> s.equalsIgnoreCase("Keep-Alive"));
        }
        ProtocolVersion protocolVersion = getRequestLine().getProtocolVersion();
        return "HTTP/1.1".equals(protocolVersion.toString());
    }

    public int contentLength() {
        if (headers != null && headers.get("Content-Length") != null) {
            return Integer.valueOf(headers.get("Content-Length").get(0));
        }
        return 0;
    }
}
