package org.iac2.common.utility;

import java.net.URI;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public class Utils {

    public static int DOCKERHTTPCLIENT_MAXCONNECTIONS = 100;

    public static DockerClient createDockerClient(String dockerEngineUrl) {
        DefaultDockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerEngineUrl)
                .withDockerTlsVerify(false)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(DOCKERHTTPCLIENT_MAXCONNECTIONS)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(dockerConfig, httpClient);
    }

    private static URI parseUrl(String value) {
        try {
            return new URI(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isIp(String value) {
        if (value == null) {
            return false;
        }

        String IPV4_PATTERN = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}" +
                "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
        final Pattern pattern = Pattern.compile(IPV4_PATTERN);
        final Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    private static boolean isLocalhost(String value) {
        return "localhost".equals(value);
    }

    public static String extractHost(String value) {
        if (isIp(value) || isLocalhost(value)) {
            return value;
        }

        URI url = parseUrl(value);
        return url != null ? url.getHost() : null;
    }
}
