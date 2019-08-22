package config;

import lombok.Getter;

@Getter
public class Config {
    public static final int maxReqHeaders = 40;
    public static int defaultServerPort = 9000;
    public static int defaultCorePoolSize = 20;
    public static int defaultMaxPoolSize = 50;
    public static int defaultKeepAliveTimeInSeconds = 120;
}
