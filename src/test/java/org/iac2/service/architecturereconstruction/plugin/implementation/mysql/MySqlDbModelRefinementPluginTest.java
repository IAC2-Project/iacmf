package org.iac2.service.architecturereconstruction.plugin.implementation.mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.EdmmTypeResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

class MySqlDbModelRefinementPluginTest {
    final static String connectionString = "jdbc:mysql://localhost:3306/iac2?user=root&password=rootpassword";
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlDbModelRefinementPluginTest.class);

    @BeforeEach
    void init() throws SQLException {
        EdmmTypeResolver.initDefaultMappings();
    }

    @Test
    void getUsersOfDatabase() throws SQLException {
        String ip = "localhost";
        String port = "3306";
        String user = "root";
        String pass = "rootpassword";
        String db = "iac2";

        Collection<String> users = MySqlDbModelRefinementPlugin.getUsersOfDatabase(ip, port, user, pass, db);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals("iac2-admin", users.stream().findFirst().orElseThrow());
    }

    @Test
    void testEnhancementOnManualInstanceModel() throws IOException, SQLException {
        ClassPathResource resource = new ClassPathResource("edmm/self_instance_model.yaml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        MySqlDbModelRefinementPlugin plugin = new MySqlDbModelRefinementPlugin();
        plugin.setConfigurationEntry(MySqlDbModelRefinementPlugin.CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES, String.valueOf(false));
        InstanceModel result =
                plugin.enhanceModel(new InstanceModel(model), new ProductionSystem("", "", new HashMap<>()));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.getDeploymentModel().getComponents().size());
        Assertions.assertTrue(result.getDeploymentModel().getComponent("iac2db").isPresent());
        RootComponent component = result.getDeploymentModel().getComponent("iac2db").orElseThrow();
        Assertions.assertTrue(component.getProperty(MySqlDbModelRefinementPlugin.EDMM_PROPERTY_NAME_USERS).isPresent());
        Property property = component.getProperty(MySqlDbModelRefinementPlugin.EDMM_PROPERTY_NAME_USERS).orElseThrow();
        String value = property.getValue();
        Assertions.assertNotNull(value);
        Assertions.assertEquals("iac2-admin", value);
    }

    @Test
    void testEnhancementOnManualInstanceModelTwoUsers() throws IOException, SQLException {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement addUserS = conn.prepareStatement("CREATE USER 'ghareeb' IDENTIFIED WITH mysql_native_password BY 'ghareeb';");
            addUserS.executeUpdate();
            PreparedStatement grantS = conn.prepareStatement("GRANT SELECT ON iac2.* TO 'ghareeb';");
            grantS.executeUpdate();
            ClassPathResource resource = new ClassPathResource("edmm/self_instance_model.yaml");
            DeploymentModel model = DeploymentModel.of(resource.getFile());
            MySqlDbModelRefinementPlugin plugin = new MySqlDbModelRefinementPlugin();
            plugin.setConfigurationEntry(MySqlDbModelRefinementPlugin.CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES, String.valueOf(false));
            InstanceModel result =
                    plugin.enhanceModel(new InstanceModel(model), new ProductionSystem("", "", new HashMap<>()));
            Assertions.assertNotNull(result);
            Assertions.assertEquals(4, result.getDeploymentModel().getComponents().size());
            Assertions.assertTrue(result.getDeploymentModel().getComponent("iac2db").isPresent());
            RootComponent component = result.getDeploymentModel().getComponent("iac2db").orElseThrow();
            Assertions.assertTrue(component.getProperty(MySqlDbModelRefinementPlugin.EDMM_PROPERTY_NAME_USERS).isPresent());
            Property property = component.getProperty(MySqlDbModelRefinementPlugin.EDMM_PROPERTY_NAME_USERS).orElseThrow();
            String value = property.getValue();
            Assertions.assertNotNull(value);
            Set<String> users = Set.of(value.split(","));
            Assertions.assertEquals(2, users.size());
            Assertions.assertTrue(users.contains("iac2-admin"));
            Assertions.assertTrue(users.contains("ghareeb"));
        } finally {
            try (Connection conn = DriverManager.getConnection(connectionString)) {
                PreparedStatement remUserS = conn.prepareStatement("DROP USER 'ghareeb'");
                remUserS.executeUpdate();
            } catch (SQLException e) {
                LOGGER.warn(e.toString());
            }
        }
    }
}