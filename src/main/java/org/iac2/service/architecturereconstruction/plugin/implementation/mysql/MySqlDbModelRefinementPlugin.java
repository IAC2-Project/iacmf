package org.iac2.service.architecturereconstruction.plugin.implementation.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPlugin;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDb;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDbms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// see: https://serverfault.com/questions/263868/how-to-know-all-the-users-that-can-access-a-database-mysql
// see: https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
// see: https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-connect-drivermanager.html
public class MySqlDbModelRefinementPlugin implements ModelRefinementPlugin {
    public static final String CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES = "ignoreMissingProperties";
    public static final String EDMM_PROPERTY_NAME_USERS = "users";
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlDbModelRefinementPlugin.class);
    private final MySqlDbModelRefinementPluginDescriptor descriptor;
    private boolean ignoreMissingProperties;

    public MySqlDbModelRefinementPlugin(MySqlDbModelRefinementPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public static Collection<String> getUsersOfDatabase(String ip, String port, String user, String password, String database) throws SQLException {
        final String connectionString = String.format("jdbc:mysql://%s:%s/mysql?user=%s&password=%s", ip, port, user, password);
        Collection<String> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionString)) {
            final String statementSQL = """
                     SELECT user FROM mysql.db WHERE db='%s'
                     UNION
                     SELECT user FROM mysql.tables_priv WHERE db='%s'
                     UNION
                     SELECT user FROM mysql.columns_priv WHERE db='%s'
                     UNION
                     SELECT user FROM mysql.procs_priv WHERE db='%s'
                    """.formatted(database, database, database, database);
            try (Statement statement = conn.createStatement()) {
                ResultSet resultSet = statement.executeQuery(statementSQL);

                while (resultSet.next()) {
                    result.add(resultSet.getString(1));
                }
            }
        } catch (SQLException ex) {
            // handle any errors
            LOGGER.error("SQLException: " + ex.getMessage());
            LOGGER.error("SQLState: " + ex.getSQLState());
            LOGGER.error("VendorError: " + ex.getErrorCode());

            throw ex;
        }

        return result;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        if (CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES.equals(inputName)) {
            this.ignoreMissingProperties = Boolean.parseBoolean(inputValue);
        } else {
            LOGGER.warn("Trying to pass a user input not expected by the plugin!");
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        if (CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES.equals(name)) {
            return String.valueOf(ignoreMissingProperties);
        }

        LOGGER.warn("Trying to get unknown user input from the plugin!");
        return null;
    }

    @Override
    public InstanceModel refineModel(InstanceModel instanceModel, ProductionSystem productionSystem) {
        Collection<MySqlDbms> dbmss = Edmm.getAllComponentsOfType(instanceModel.getDeploymentModel(), MySqlDbms.class);

        try {
            for (MySqlDbms dbms : dbmss) {
                String userName = dbms.getProperty(MySqlDbms.DBMSUser).orElse(null);
                String password = dbms.getProperty(MySqlDbms.DBMSPassword).orElse(null);
                String port = dbms.getProperty(MySqlDbms.DBMSPort).orElse(null);
                String ip = Edmm.findHostIp(dbms, instanceModel.getDeploymentModel());

                if (ip != null && userName != null && password != null && port != null) {
                    Collection<RootComponent> dbs = Edmm.findSourceComponents(instanceModel.getDeploymentModel(), dbms, HostedOn.class);

                    for (RootComponent db : dbs) {
                        if (db.getProperty(MySqlDb.DBName).isPresent()) {
                            String dbName = db.getProperty(MySqlDb.DBName).get();
                            Collection<String> users = getUsersOfDatabase(ip, port, userName, password, dbName);
                            Edmm.addPropertyAssignments(instanceModel.getDeploymentModel().getGraph(),
                                    db.getEntity().getId(), Map.of(EDMM_PROPERTY_NAME_USERS, users));
                        } else if (!ignoreMissingProperties) {
                            throw new MalformedInstanceModelException(db.getName(), MySqlDb.DBName.getName(),
                                    String.format("The plugin (id: %s) is trying to access a missing property (name: %s)" +
                                                    " in the component (id: %s) of the reconstructed instance model.",
                                            getIdentifier(), db.getName(), MySqlDb.DBName.getName()));
                        }
                    }
                } else if (!ignoreMissingProperties) {
                    String missingPropertyName =
                            userName == null ? MySqlDbms.DBMSUser.getName() :
                                    password == null ? MySqlDbms.DBMSPassword.getName() :
                                            port == null ? MySqlDbms.DBMSPort.getName() : "IP";
                    throw new MalformedInstanceModelException(dbms.getName(), missingPropertyName,
                            String.format("The plugin (id: %s) is trying to access a missing property (name: %s)" +
                                            " in the component (id: %s) of the reconstructed instance model.",
                                    getIdentifier(), dbms.getName(), missingPropertyName));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new InstanceModel(new DeploymentModel(
                instanceModel.getDeploymentModel().getName(),
                instanceModel.getDeploymentModel().getGraph()));
    }
}
