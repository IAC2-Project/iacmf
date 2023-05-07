package org.iac2.service.fixing.plugin.implementaiton.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.parameter.StringCollectionComplianceRuleParameter;
import org.iac2.common.utility.Edmm;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDb;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDbms;
import org.iac2.service.fixing.common.exception.ComplianceRuleMissingRequiredParameterException;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.common.model.compliancejob.issue.IssueFixingReport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveDBUsersFixingPlugin implements IssueFixingPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDBUsersFixingPlugin.class);
    private static final String MODEL_PROPERTY_NAME_USERS = "users";

    private final RemoveDBUsersFixingPluginDescriptor descriptor;

    public RemoveDBUsersFixingPlugin(RemoveDBUsersFixingPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public static void removeDatabaseUsers(String userName, String password, String port, String ip, String dbName, Set<String> usersToRemove) throws SQLException {
        final String connectionString = String.format("jdbc:mysql://%s:%s/mysql?user=%s&password=%s", ip, port, userName, password);

        try (Connection conn = DriverManager.getConnection(connectionString)) {
            for (String user : usersToRemove) {
                PreparedStatement remUserS = conn.prepareStatement("REVOKE ALL ON %s.* FROM '%s'@'%%'".formatted(dbName, user));
                remUserS.executeUpdate();
            }
        }
    }

    @NotNull
    public static Set<String> findUsersToRemove(ComplianceIssue issue, String modelComponentName, RootComponent db) {
        // Find out which users to remove
        Collection<String> currentUsers = Edmm.getAttributeValueAsList(db, MODEL_PROPERTY_NAME_USERS);

        if (currentUsers == null) {
            throw new MalformedInstanceModelException(modelComponentName, MODEL_PROPERTY_NAME_USERS,
                    "The component '%s' in the instance model is missing a required property: '%s'".formatted(db, MODEL_PROPERTY_NAME_USERS));
        }

        final String CR_PROPERTY_NAME = RemoveDBUsersFixingPluginDescriptor.EXPECTED_COMPLIANCE_RULE_PARAMETERS[0];
        StringCollectionComplianceRuleParameter parameter = (StringCollectionComplianceRuleParameter) issue.getRule()
                .getParameterAssignments()
                .stream()
                .filter(p -> p.getName().equals(CR_PROPERTY_NAME))
                .findFirst()
                .orElseThrow();
        Collection<String> allowed = parameter.getValue();
        Set<String> usersToRemove = new HashSet<>(currentUsers);
        usersToRemove.removeAll(allowed);
        return usersToRemove;
    }

    @NotNull
    public static ConnectionInformation getConnectionInformation(InstanceModel model, RootComponent db) {
        Collection<RootComponent> dbmss = Edmm.findTargetComponents(model.getDeploymentModel(), db, HostedOn.class);

        if (dbmss == null || dbmss.size() != 1 || !(dbmss.stream().findFirst().get() instanceof MySqlDbms dbms)) {
            throw new MalformedInstanceModelException(null, null,
                    "Cannot find the DBMS component hosting the database: %s".formatted(db.getName()));
        }

        String userNameS = dbms.getProperty(MySqlDbms.DBMSUser).orElse(null);
        String passwordS = dbms.getProperty(MySqlDbms.DBMSPassword).orElse(null);
        String portS = dbms.getProperty(MySqlDbms.DBMSPort).orElse(null);
        String ipS = Edmm.findHostIp(dbms, model.getDeploymentModel());
        String dbNameS = db.getProperty(MySqlDb.DBName).orElse(null);

        return new ConnectionInformation(userNameS, passwordS, portS, ipS, dbNameS, dbms);
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        LOGGER.warn("Trying to set user input in a plugin that does not expect user inputs!");
    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) {
        // Validate some inputs
        validateInputs(issue, productionSystem);

        String modelComponentName = issue.getProperties().get("CHECKER_COMPONENT_ID");
        RootComponent db = model.getDeploymentModel()
                .getComponent(modelComponentName)
                .orElse(null);

        if (db == null) {
            throw new MalformedInstanceModelException(modelComponentName, null, "Cannot find component referenced in issue!");
        }

        // Find out which users need to be removed
        Set<String> usersToRemove = findUsersToRemove(issue, modelComponentName, db);

        if (usersToRemove.size() == 0) {
            new IssueFixingReport(true, "No users found to be removed!");
        }

        // Find out how to connect to the database
        ConnectionInformation connectionInformation = getConnectionInformation(model, db);

        String userName = connectionInformation.userName;
        String ip = connectionInformation.ip;
        String password = connectionInformation.password;
        String port = connectionInformation.port;
        String dbName = connectionInformation.dbName;

        if (ip != null && userName != null && password != null && port != null && dbName != null) {
            try {
                removeDatabaseUsers(userName, password, port, ip, dbName, usersToRemove);
                String removedUsersAsString = usersToRemove.stream()
                        .map("'%s'"::formatted)
                        .collect(Collectors.joining(","));
                return new IssueFixingReport(true,
                        "Successfully revoked all permissions of users: %s from the database: '%s' (host: '%s')."
                                .formatted(removedUsersAsString, dbName, ip));
            } catch (SQLException e) {
                return new IssueFixingReport(false, "Failed to remove unexpected users. Reason: %s".formatted(e.getMessage()));
            }
        } else {
            String missingPropertyName =
                    dbName == null ? MySqlDb.DBName.getName() :
                            userName == null ? MySqlDbms.DBMSUser.getName() :
                                    password == null ? MySqlDbms.DBMSPassword.getName() :
                                            port == null ? MySqlDbms.DBMSPort.getName() : "IP";
            throw new MalformedInstanceModelException(connectionInformation.dbms.getName(), missingPropertyName,
                    String.format("The plugin (id: %s) is trying to access a missing property (name: %s)" +
                                    " in the component (id: %s) of the reconstructed instance model.",
                            getIdentifier(), connectionInformation.dbms.getName(), missingPropertyName));
        }
    }

    public void validateInputs(ComplianceIssue issue, ProductionSystem productionSystem) {
        if (!descriptor.isIssueTypeSupported(issue.getType())) {
            throw new IssueNotSupportedException(issue.getType());
        }

        if (!issue.getProperties().containsKey("CHECKER_COMPONENT_ID")) {
            throw new IssueNotSupportedException(issue.getType(),
                    "The issue is missing a required property: '%s'".formatted("CHECKER_COMPONENT_ID"));
        }

        if (!descriptor.isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        String missingCRParam = descriptor.getRequiredComplianceRuleParameters()
                .stream()
                .filter(pName -> issue.getRule().getParameterAssignments().stream().noneMatch(p -> p.getName().equals(pName)))
                .findFirst().orElse(null);

        if (missingCRParam != null) {
            throw new ComplianceRuleMissingRequiredParameterException(issue.getRule(), missingCRParam);
        }
    }

    static class ConnectionInformation {
        String userName;
        String password;
        String port;
        String ip;
        String dbName;
        MySqlDbms dbms;

        public ConnectionInformation(String userName, String password, String port, String ip, String dbName, MySqlDbms dbms) {
            this.userName = userName;
            this.password = password;
            this.port = port;
            this.ip = ip;
            this.dbName = dbName;
            this.dbms = dbms;
        }
    }
}
