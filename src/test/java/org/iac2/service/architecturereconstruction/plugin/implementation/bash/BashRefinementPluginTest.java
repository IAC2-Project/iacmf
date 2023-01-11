package org.iac2.service.architecturereconstruction.plugin.implementation.bash;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import org.apache.commons.io.FileUtils;
import org.iac2.common.exception.IacmfException;
import org.iac2.common.exception.MalformedConfigurationEntryException;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.exception.MissingProductionSystemPropertyException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.exception.BashCommandExecutionException;
import org.iac2.service.architecturereconstruction.common.exception.WrongOutputTypeException;
import org.iac2.util.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@EnabledIf("isPrivateKeyAccessible")
class BashRefinementPluginTest {
    final static String host = "193.196.53.165";
    final static String user = "ubuntu";
    static String privateKeyPath;

    public static boolean isPrivateKeyAccessible() {
        String privateKeyContent = System.getenv(TestUtils.ENV_VARIABLE);

        return privateKeyContent != null;
    }

    @BeforeAll
    public static void init() throws IOException {
        privateKeyPath = TestUtils.fetchAndStorePrivateKeyTemporarily();
    }

    @AfterAll
    public static void teardown() throws IOException {
        if (privateKeyPath != null) {
            File file = new File(privateKeyPath);
            FileUtils.forceDelete(file);
        }
    }

    private static ProductionSystem createDummyProductionSystem(Map<String, String> props) {
        return new ProductionSystem("dummy", "dummy", props);
    }

    private static InstanceModel createDummyInstanceModel(int numNodes, int numCompute, int numbUbuntu, int numMissingAddress, int numMissingPKey) throws IllegalAccessException {
        Assertions.assertTrue(numNodes >= numCompute);
        Assertions.assertTrue(numCompute >= numbUbuntu);
        Assertions.assertTrue(numbUbuntu >= numMissingAddress + numMissingPKey);
        EntityGraph graph = new EntityGraph();

        for (int i = 1; i <= numMissingAddress; i++) {
            addNode(graph, "mA%d".formatted(i), Compute.class, "linux", "ubuntu", "", privateKeyPath);
        }

        for (int i = 1; i <= numMissingPKey; i++) {
            addNode(graph, "mP%d".formatted(i), Compute.class, "linux", "ubuntu", host, "");
        }

        for (int i = 1; i <= numbUbuntu - (numMissingPKey + numMissingAddress); i++) {
            addNode(graph, "u%d".formatted(i), Compute.class, "linux", "ubuntu", host, privateKeyPath);
        }

        for (int i = 1; i <= numCompute - numbUbuntu; i++) {
            addNode(graph, "c%d".formatted(i), Compute.class, "linux", "fedora", host, privateKeyPath);
        }

        for (int i = 1; i <= numNodes - numCompute; i++) {
            addNode(graph, "n%d".formatted(i), Paas.class, null, null, null, null);
        }

        DeploymentModel model = new DeploymentModel("dummy", graph);
        Assertions.assertEquals(numNodes, model.getComponents().size());

        return new InstanceModel(model);
    }

    private static BashRefinementPlugin createPlugin(String script, String userName, String outputPropertyName, String outputPropertyType, String productionSystemArguments, String defaultPK, String ignore) {
        BashRefinementPlugin plugin = new BashRefinementPlugin(new BashRefinementPluginDescriptor());
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_USERNAME, userName);
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT, script);
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME, outputPropertyName);
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE, outputPropertyType);
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS, productionSystemArguments);
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY, defaultPK);
        plugin.setConfigurationEntry(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES, ignore);

        return plugin;
    }

    private static void addNode(EntityGraph entityGraph, String id, Class<? extends RootComponent> clazz, String osF, String image, String address, String privateKeyPath) throws IllegalAccessException {

        Map<String, Object> atts = clazz.isAssignableFrom(Compute.class) ? Map.of(
                Compute.OS_FAMILY.getName(), osF,
                Compute.MACHINE_IMAGE.getName(), image,
                Compute.PUBLIC_ADDRESS.getName(), address,
                Compute.PRIVATE_KEY_PATH.getName(), privateKeyPath) : Map.of();
        Edmm.addComponent(entityGraph, id, atts, clazz);
    }

    public static Stream<Arguments> configurationEntriesProvider() {
        return Stream.of(
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT, "abc", "abc", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS, null, null, null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS, "a,b", "a,b", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_USERNAME, "abc", "abc", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY, "abc", "abc", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME, "abc", "abc", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE, null, null, null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE, "apple", null, MalformedConfigurationEntryException.class),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE, "INT", "INT", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES, "tRuE", "true", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES, "FALSe", "false", null),
                Arguments.of(BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES, null, "false", null)
        );
    }

    public static Stream<Arguments> invalidInputsProvider() throws IllegalAccessException {
        return Stream.of(
                Arguments.of(
                        createPlugin("", "user", "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin(null, "user", "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "", "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", null, "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", null, "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", null, "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingConfigurationEntryException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("pasa", "Hello world!")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingProductionSystemPropertyException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        MissingProductionSystemPropertyException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", "psa", "somewhere", "false"),
                        createDummyProductionSystem(Map.of("psa", "hello world")),
                        createDummyInstanceModel(1, 1, 1, 0, 0),
                        null,
                        1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, "somewhere", "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(1, 1, 0, 0, 0),
                        null,
                        0
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, "somewhere", "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 0, 0),
                        null,
                        2
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, "somewhere", "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 0, 2),
                        null,
                        2
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 0, 0),
                        null,
                        2
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "true"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 0, 1),
                        null,
                        1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "true"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 1, 0),
                        null,
                        1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "true"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 1, 1),
                        null,
                        0
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 0, 1),
                        MalformedInstanceModelException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 1, 0),
                        MalformedInstanceModelException.class,
                        -1
                ),
                Arguments.of(
                        createPlugin("dir", "user", "abc", "INT", null, null, "false"),
                        createDummyProductionSystem(Map.of()),
                        createDummyInstanceModel(2, 2, 2, 1, 1),
                        MalformedInstanceModelException.class,
                        -1
                )

        );
    }

    @Test
    void testRealWorldScenario() throws Exception {
        final String command = "[[ ! -z $(sudo grep nullok /etc/pam.d/common-password) ]] && echo 'true' || echo 'false'";
        TestUtils.setupRealWorldScenario(host, user, privateKeyPath);

        try {
            BashRefinementPlugin plugin = createPlugin(command, user, "allowsNulls", "BOOLEAN", null, null, "false");
            ProductionSystem productionSystem = createDummyProductionSystem(Map.of());
            InstanceModel instanceModel = createDummyInstanceModel(1, 1, 1, 0, 0);
            instanceModel = plugin.refineModel(instanceModel, productionSystem);

            Assertions.assertNotNull(instanceModel);
            Compute compute = (Compute) instanceModel.getDeploymentModel().getComponent("u1").orElseThrow();
            Assertions.assertTrue(compute.getProperty("allowsNulls").isPresent());
            Assertions.assertEquals(EdmmTypeResolver.resolveBasicType(Boolean.class), compute.getProperty("allowsNulls").get().getType());
            Assertions.assertEquals("true", compute.getProperty("allowsNulls").get().getValue());
        } finally {
            TestUtils.teardownRealWorldScenario(host, user, privateKeyPath);
        }
    }

    @Test
    void testRefinement() throws IllegalAccessException {
        InstanceModel instanceModel = createDummyInstanceModel(2, 2, 0, 0, 0);
        BashRefinementPlugin plugin = createPlugin("echo 'Hello World!'", user, "statement", "STRING", null, privateKeyPath, "false");
        ProductionSystem ps = createDummyProductionSystem(Map.of());
        instanceModel = plugin.refineModel(instanceModel, ps);
        Assertions.assertEquals(2, instanceModel.getDeploymentModel().getComponents().size());
        // none of the components in the model has a property called "statement"
        Assertions.assertTrue(instanceModel.getDeploymentModel().getComponents().stream().noneMatch(c -> c.getProperties().keySet().stream().anyMatch("statement"::equals)));

        instanceModel = createDummyInstanceModel(1, 1, 1, 0, 0);
        plugin = createPlugin("echo 'Hello World!'", user, "statement", "STRING", null, null, "false");
        instanceModel = plugin.refineModel(instanceModel, ps);
        Assertions.assertEquals(1, instanceModel.getDeploymentModel().getComponents().size());
        Compute compute = (Compute) instanceModel.getDeploymentModel().getComponent("u1").orElseThrow();
        Assertions.assertTrue(compute.getProperty("statement").isPresent());
        Assertions.assertEquals("Hello World!", compute.getProperty("statement").get().getValue());

        instanceModel = createDummyInstanceModel(2, 2, 2, 0, 0);
        plugin = createPlugin("echo 'Hello World!'", user, "statement", "STRING", null, null, "false");
        instanceModel = plugin.refineModel(instanceModel, ps);
        Collection<RootComponent> computes = instanceModel.getDeploymentModel().getComponents();
        Assertions.assertEquals(2, computes.size());

        for (RootComponent c : computes) {
            Assertions.assertTrue(c.getProperty("statement").isPresent());
            Assertions.assertEquals("Hello World!", c.getProperty("statement").get().getValue());
        }

        instanceModel = createDummyInstanceModel(1, 1, 1, 0, 0);
        plugin = createPlugin("echo 'Hello World!'", user, "statement", "STRING", "A,B", null, "false");
        ProductionSystem ps2 = createDummyProductionSystem(Map.of("A", "aa!", "B", "bb"));
        instanceModel = plugin.refineModel(instanceModel, ps2);
        Assertions.assertEquals(1, instanceModel.getDeploymentModel().getComponents().size());
        compute = (Compute) instanceModel.getDeploymentModel().getComponent("u1").orElseThrow();
        Assertions.assertTrue(compute.getProperty("statement").isPresent());
        Assertions.assertEquals("Hello World! aa! bb", compute.getProperty("statement").get().getValue());

        InstanceModel instanceModel1 = createDummyInstanceModel(1, 1, 1, 0, 0);
        BashRefinementPlugin plugin1 = createPlugin("echo 'Hello World!'", "notrealuser", "statement", "STRING", "A,B", null, "false");
        Assertions.assertThrows(BashCommandExecutionException.class, () -> plugin1.refineModel(instanceModel1, ps2));

        InstanceModel instanceModel2 = createDummyInstanceModel(1, 1, 1, 0, 0);
        BashRefinementPlugin plugin2 = createPlugin("echo 'Hello World!'", user, "statement", "INT", "A,B", null, "false");
        Assertions.assertThrows(WrongOutputTypeException.class, () -> plugin2.refineModel(instanceModel2, ps2));
    }

    @ParameterizedTest
    @MethodSource("configurationEntriesProvider")
    void testSetConfigEntry(String name, String value, String outcome, Class<? extends Exception> clazz) {
        BashRefinementPlugin plugin = new BashRefinementPlugin(new BashRefinementPluginDescriptor());

        if (clazz != null) {
            Assertions.assertThrows(clazz, () -> plugin.setConfigurationEntry(name, value));
        } else {
            Assertions.assertDoesNotThrow(() -> plugin.setConfigurationEntry(name, value));
            Assertions.assertEquals(outcome, plugin.getConfigurationEntry(name));
        }
    }

    @ParameterizedTest
    @MethodSource("invalidInputsProvider")
    void testParamValidation(BashRefinementPlugin plugin, ProductionSystem productionSystem, InstanceModel instanceModel, Class<? extends IacmfException> clazz, int resultSize) {
        if (clazz != null) {
            Assertions.assertThrows(clazz, () -> plugin.validateInputs(instanceModel, productionSystem));
        } else {
            Collection<Compute> compute = plugin.validateInputs(instanceModel, productionSystem);
            Assertions.assertNotNull(compute);
            Assertions.assertEquals(resultSize, compute.size());
        }
    }
}