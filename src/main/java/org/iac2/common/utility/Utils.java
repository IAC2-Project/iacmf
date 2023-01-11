package org.iac2.common.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Compute;
import org.h2.store.fs.FileUtils;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.exception.PrivateKeyNotAccessibleException;
import org.iac2.service.fixing.plugin.implementaiton.bash.BashFixingPluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
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

    public static DeploymentModel fetchEdmmDeploymentModel(String fullUrl) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(fullUrl))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String edmmModel = response.body();
        return DeploymentModel.of(edmmModel);
    }

    public static String readPrivateKey(Compute vmComponent, String defaultPrivateKeyPath, String pluginId) {
        String privateKeyPath = vmComponent.getProperty(Compute.PRIVATE_KEY_PATH).filter(p -> !p.isEmpty()).orElse(null);

        if (privateKeyPath == null) {
            LOGGER.warn("The vm component (id: {}) does not declare a path to a private key file. Looking for a default file instead!", vmComponent.getId());

            if (defaultPrivateKeyPath == null) {
                throw new MissingConfigurationEntryException(pluginId, BashFixingPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY);
            }

            privateKeyPath = defaultPrivateKeyPath;
        }

        if (!FileUtils.exists(privateKeyPath)) {
            throw new PrivateKeyNotAccessibleException(new FileNotFoundException("The file '%s' does not exist!".formatted(privateKeyPath)));
        }

        try {
            return Files.readString(Path.of(privateKeyPath));
        } catch (IOException e) {
            throw new PrivateKeyNotAccessibleException(e);
        }
    }
}
