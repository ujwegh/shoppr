package ru.nik.commons.http.config;

import lombok.Data;

@Data
public class WebClientConfigurationProperties {

    private int maxConnections = 100;
    private boolean isKeepAlive = false;

    /**
     * timeouts in millis
     */
    private int acquireTimeout = 100;
    private int connectTimeout = 10000;
    private int readTimeout = 10000;
    private int writeTimeout = 10000;

    private String maxBufferSize = "12MB";
}
