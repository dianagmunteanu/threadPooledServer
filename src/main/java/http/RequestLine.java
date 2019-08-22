package http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestLine {

    private Method method;
    private String uri;
    private ProtocolVersion protocolVersion;
}
