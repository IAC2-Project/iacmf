package org.iac2.service.fixing.plugin.implementaiton.mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDb;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDbms;
import org.iac2.service.fixing.common.exception.ComplianceRuleMissingRequiredParameterException;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

class RemoveDBUsersFixingPluginTest {

    private final static String DB_COMPONENT = "MySQL-DB_w1_0";
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDBUsersFixingPluginTest.class);

    @BeforeEach
    private void init() {
        EdmmTypeResolver.initDefaultMappings();
    }

    @Test
    void testFixing() throws IOException, SQLException {
        Collection<String> instanceModelUsers = List.of("iac2-admin", "ghareeb");
        Collection<String> ruleUsers = List.of("iac2-admin");
        String dbComponent = "iac2db";
        RemoveDBUsersFixingPlugin plugin = new RemoveDBUsersFixingPlugin(new RemoveDBUsersFixingPluginDescriptor());
        ProductionSystem productionSystem = new ProductionSystem("any", "any", new HashMap<>());
        InstanceModel instanceModel = createInstanceModel(instanceModelUsers, "edmm/self_instance_model.yaml", dbComponent);
        final String connectionString = "jdbc:mysql://localhost:3306/iac2?user=root&password=rootpassword";

        try (Connection conn = DriverManager.getConnection(connectionString)) {
            // add an un-expected user
            PreparedStatement addUserS = conn.prepareStatement("CREATE USER 'ghareeb' IDENTIFIED WITH mysql_native_password BY 'ghareeb';");
            addUserS.executeUpdate();
            PreparedStatement grantS = conn.prepareStatement("GRANT SELECT ON iac2.* TO 'ghareeb';");
            grantS.executeUpdate();

            // successful revoking
            ComplianceIssue issue = createComplianceIssue(ruleUsers, dbComponent);
            IssueFixingReport report = plugin.fixIssue(issue, instanceModel, productionSystem);
            Assertions.assertNotNull(report);
            Assertions.assertNotNull(report.getDescription());
            Assertions.assertTrue(report.getDescription().length() > 0);
            LOGGER.info(report.getDescription());
            Assertions.assertTrue(report.isSuccessful());

            //failed revoking (wrong password)
            RootComponent component = instanceModel.getDeploymentModel().getComponent(dbComponent).orElseThrow();
            RootComponent dbms = RemoveDBUsersFixingPlugin.getConnectionInformation(instanceModel, component).dbms;
            Edmm.addPropertyAssignments(instanceModel.getDeploymentModel().getGraph(), dbms.getEntity().getId(), Map.of(MySqlDbms.DBMSPassword.getName(), "abc"));
            instanceModel = new InstanceModel(new DeploymentModel(instanceModel.getDeploymentModel().getName(), instanceModel.getDeploymentModel().getGraph()));
            report = plugin.fixIssue(issue, instanceModel, productionSystem);
            Assertions.assertNotNull(report);
            Assertions.assertNotNull(report.getDescription());
            Assertions.assertTrue(report.getDescription().length() > 0);
            LOGGER.info(report.getDescription());
            Assertions.assertFalse(report.isSuccessful());
        } finally {
            // undo ddl changes (rollback does not work with such statements)
            try (Connection conn = DriverManager.getConnection(connectionString)) {
                PreparedStatement remUserS = conn.prepareStatement("DROP USER 'ghareeb'");
                remUserS.executeUpdate();
            } catch (SQLException e) {
                LOGGER.warn(e.toString());
            }
        }

        // throw exception (missing db component)
        RootComponent component = instanceModel.getDeploymentModel().getComponent("iac2db").orElseThrow();
        Edmm.removeComponents(instanceModel.getDeploymentModel().getGraph(), List.of(component));
        final InstanceModel instanceModel1 = new InstanceModel(new DeploymentModel(instanceModel.getDeploymentModel().getName(),
                instanceModel.getDeploymentModel().getGraph()));
        ComplianceIssue issue = createComplianceIssue(ruleUsers, dbComponent);
        Assertions.assertThrows(MalformedInstanceModelException.class, () -> plugin.fixIssue(issue, instanceModel1, productionSystem));
    }

    @Test
    void testGetConnectionInfo() throws IOException {
        InstanceModel instanceModel = createInstanceModel(new ArrayList<>());
        RootComponent db = instanceModel.getDeploymentModel().getComponent(DB_COMPONENT).orElseThrow();
        RemoveDBUsersFixingPlugin.ConnectionInformation info = RemoveDBUsersFixingPlugin.getConnectionInformation(instanceModel, db);
        Assertions.assertNotNull(info);
        Assertions.assertNotNull(info.dbms);
        Assertions.assertNotNull(info.ip);
        Assertions.assertNotNull(info.dbName);
        Assertions.assertNotNull(info.userName);
        Assertions.assertNotNull(info.port);
        Assertions.assertNotNull(info.password);
        Assertions.assertEquals("root", info.userName);
        Assertions.assertEquals("172.17.0.1", info.ip);
        Assertions.assertEquals("realWorld", info.dbName);
        Assertions.assertEquals("3306", info.port);
        Assertions.assertEquals("root", info.password);

        Edmm.removeComponents(instanceModel.getDeploymentModel().getGraph(), List.of(info.dbms));
        final InstanceModel instanceModel2 = new InstanceModel(new DeploymentModel(instanceModel.getDeploymentModel().getName(),
                instanceModel.getDeploymentModel().getGraph()));
        final RootComponent db2 = instanceModel2.getDeploymentModel().getComponent(DB_COMPONENT).orElseThrow();
        Assertions.assertThrows(MalformedInstanceModelException.class, () -> RemoveDBUsersFixingPlugin.getConnectionInformation(instanceModel2, db2));
    }

    @Test
    void testFindUsersToRemove() throws IOException {
        // no users to remove
        InstanceModel instanceModel = createInstanceModel(List.of("a", "b"));
        RootComponent db = instanceModel.getDeploymentModel().getComponent(DB_COMPONENT).orElseThrow();
        ComplianceIssue issue = createComplianceIssue(null, null);
        Assertions.assertEquals(0, RemoveDBUsersFixingPlugin.findUsersToRemove(issue, db.getName(), db).size());

        // two users to remove
        Edmm.addPropertyAssignments(instanceModel.getDeploymentModel().getGraph(), db.getEntity().getId(), Map.of(MySqlDb.users.getName(),
                List.of("a", "b", "x", "y")));
        instanceModel.setDeploymentModel(new DeploymentModel(instanceModel.getDeploymentModel().getName(), instanceModel.getDeploymentModel().getGraph()));
        db = instanceModel.getDeploymentModel().getComponent(DB_COMPONENT).orElseThrow();
        Set<String> toRemove = RemoveDBUsersFixingPlugin.findUsersToRemove(issue, db.getName(), db);
        Assertions.assertNotNull(toRemove);
        Assertions.assertEquals(2, toRemove.size());
        Assertions.assertTrue(toRemove.contains("x"));
        Assertions.assertTrue(toRemove.contains("y"));

        // missing property in instance model
        instanceModel = createInstanceModel(new ArrayList<>());
        final RootComponent db1 = instanceModel.getDeploymentModel().getComponent(DB_COMPONENT).orElseThrow();
        final String name = db1.getName();
        Assertions.assertThrows(MalformedInstanceModelException.class, () -> RemoveDBUsersFixingPlugin.findUsersToRemove(issue, name, db1));
    }

    @Test
    void testValidation() {
        RemoveDBUsersFixingPlugin plugin = new RemoveDBUsersFixingPlugin(new RemoveDBUsersFixingPluginDescriptor());
        ProductionSystem productionSystem = new ProductionSystem("blabla", "", new HashMap<>());
        final ComplianceIssue issue = createComplianceIssue(null, null);
        Assertions.assertDoesNotThrow(() -> plugin.validateInputs(issue, productionSystem));
        issue.setType("oops will not work");
        Assertions.assertThrows(IssueNotSupportedException.class, () -> plugin.validateInputs(issue, productionSystem));
        final ComplianceIssue issue2 = createComplianceIssue(null, null);
        issue2.getProperties().clear();
        Assertions.assertThrows(IssueNotSupportedException.class, () -> plugin.validateInputs(issue2, productionSystem));
        final ComplianceIssue issue3 = createComplianceIssue(null, null);
        issue3.getRule().getParameterAssignments().clear();
        Assertions.assertThrows(ComplianceRuleMissingRequiredParameterException.class, () -> plugin.validateInputs(issue3, productionSystem));
    }

    private ComplianceIssue createComplianceIssue(Collection<String> expectedUsers, String dbComponent) {
        if (expectedUsers == null) {
            expectedUsers = List.of("a", "b", "c", "d");
        }

        if (dbComponent == null) {
            dbComponent = DB_COMPONENT;
        }
        ComplianceRule complianceRule = new ComplianceRule(1L, "blabla", "somewhere", RemoveDBUsersFixingPluginDescriptor.SUPPORTED_ISSUE_TYPES[0]);
        complianceRule.addStringCollectionParameter(RemoveDBUsersFixingPluginDescriptor.EXPECTED_COMPLIANCE_RULE_PARAMETERS[0], expectedUsers);
        Map<String, String> issueProps = new HashMap<>();
        issueProps.put("CHECKER_COMPONENT_ID", dbComponent);
        return new ComplianceIssue("some issue", complianceRule, RemoveDBUsersFixingPluginDescriptor.SUPPORTED_ISSUE_TYPES[0],
                issueProps);
    }

    private InstanceModel createInstanceModel(Collection<String> usersToAdd) throws IOException {
        return createInstanceModel(usersToAdd, "edmm/realworld_application_instance_model_docker_refined.yaml", DB_COMPONENT);
    }

    private InstanceModel createInstanceModel(Collection<String> usersToAdd, String path, String dbComponentName) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        RootComponent component = model.getComponent(dbComponentName).orElseThrow();
        model = new DeploymentModel(model.getName(), model.getGraph());

        if (!usersToAdd.isEmpty()) {
            Edmm.addPropertyAssignments(model.getGraph(), component.getEntity().getId(), Map.of(MySqlDb.users.getName(),
                    usersToAdd));
        }

        return new InstanceModel(model);
    }
}