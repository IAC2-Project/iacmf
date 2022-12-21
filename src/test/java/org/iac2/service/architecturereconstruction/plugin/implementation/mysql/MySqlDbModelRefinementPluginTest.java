package org.iac2.service.architecturereconstruction.plugin.implementation.mysql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.EdmmTypeResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class MySqlDbModelRefinementPluginTest {

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
    void testEnhancementOnSelfInstanceModel() throws IOException {
        EdmmTypeResolver.initDefaultMappings();
        ClassPathResource resource = new ClassPathResource("edmm/self_instance_model.yaml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        MySqlDbModelRefinementPlugin plugin = new MySqlDbModelRefinementPlugin();
        plugin.setConfigurationEntry(MySqlDbModelRefinementPlugin.CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES,
                String.valueOf(false));
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
        // todo test adding new users and re-executing refinement
    }
}