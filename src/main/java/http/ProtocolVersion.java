package http;

import lombok.Getter;


@Getter
public class ProtocolVersion {

    protected Protocol protocol;
    protected int major;
    protected int minor;

    public ProtocolVersion(String ver) {
        String[] protocolVersion = ver.split("/");
        if (protocolVersion.length != 2) {
            throw new RuntimeException("KO");
        }
        protocol = Protocol.valueOf(protocolVersion[0]);
        String[] version = protocolVersion[1].split("\\.");
        major = Integer.valueOf(version[0]);
        minor = Integer.valueOf(version[1]);
    }

    @Override
    public String toString() {
        return String.format("%s/%d.%d", protocol.name(), major, minor);
    }
}