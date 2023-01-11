package org.iac2.service.fixing.plugin.implementaiton.bash;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import org.apache.commons.io.FileUtils;
import org.iac2.common.exception.IacmfException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.exception.PrivateKeyNotAccessibleException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.service.fixing.common.exception.ComplianceRuleMissingRequiredParameterException;
import org.iac2.service.fixing.common.model.IssueFixingReport;
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
class BashFixingPluginTest {
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

    public static Stream<Arguments> invalidInputsProvider() throws IllegalAccessException {
        InstanceModel instanceModel = createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu");
        EntityGraph graph = instanceModel.getDeploymentModel().getGraph();
        Edmm.addComponent(graph, "abc", new HashMap<>(), Paas.class);
        instanceModel = new InstanceModel(new DeploymentModel("test", graph));

        return Stream.of(
                Arguments.of(
                        createPlugin(null, "a", "a"),
                        createDummyIssue(Map.of("a", "a"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        MissingConfigurationEntryException.class),
                Arguments.of(
                        createPlugin("", "a", "a"),
                        createDummyIssue(Map.of("a", "a"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        MissingConfigurationEntryException.class),
                Arguments.of(
                        createPlugin("sudo dir", null, "a"),
                        createDummyIssue(Map.of("a", "a"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        MissingConfigurationEntryException.class),
                Arguments.of(
                        createPlugin("sudo dir", "", "a"),
                        createDummyIssue(Map.of("a", "a"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        MissingConfigurationEntryException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        ComplianceRuleMissingRequiredParameterException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        ComplianceRuleMissingRequiredParameterException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of(), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        ComplianceRuleMissingRequiredParameterException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), null),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        IssueNotSupportedException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), ""),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        IssueNotSupportedException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "abc"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "abc"),
                        instanceModel,
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "", "ubuntu"),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, null, "ubuntu"),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "windows", "ubuntu"),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", ""),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", null),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "fedora"),
                        MalformedInstanceModelException.class),
                Arguments.of(
                        createPlugin("sudo dir", "a", "a,b"),
                        createDummyIssue(Map.of("b", "bb", "a", "aa"), "vm1"),
                        createDummyInstanceModel("http://a.com", privateKeyPath, "linux", "ubuntu"),
                        null)
        );
    }

    private static ComplianceIssue createDummyIssue(Map<String, String> crParams, String checkerComponentId) {
        ComplianceRule rule = new ComplianceRule(-1L, "dummy", "nowhere", "issueT");
        crParams.forEach(rule::addStringParameter);
        Map<String, String> properties = new HashMap<>();

        if (checkerComponentId != null && !checkerComponentId.isEmpty()) {
            properties.put("CHECKER_COMPONENT_ID", checkerComponentId);
        }

        return new ComplianceIssue("dummy", rule, "issueT", properties);
    }

    private static BashFixingPlugin createPlugin(String script, String userName, String complianceRuleArguments) {
        BashFixingPlugin plugin = new BashFixingPlugin(new BashFixingPluginDescriptor());
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT, script);
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME, userName);
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_COMPLIANCE_RULE_ARGUMENTS, complianceRuleArguments);

        return plugin;
    }

    private static InstanceModel createDummyInstanceModel(String host, String privateKeyPath, String osName, String image) throws IllegalAccessException {
        EntityGraph graph = new EntityGraph();
        Map<String, Object> atts = new HashMap<>();

        if (host != null && !host.isEmpty()) {
            atts.put(Compute.PUBLIC_ADDRESS.getName(), host);
        }

        if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
            atts.put(Compute.PRIVATE_KEY_PATH.getName(), privateKeyPath);
        }

        if (osName != null && !osName.isEmpty()) {
            atts.put(Compute.OS_FAMILY.getName(), osName);
        }

        if (image != null && !image.isEmpty()) {
            atts.put(Compute.MACHINE_IMAGE.getName(), image);
        }

        Edmm.addComponent(graph, "vm1", atts, Compute.class);

        return new InstanceModel(new DeploymentModel("test", graph));
    }

    @Test
    void testFixing() throws IllegalAccessException {
        ComplianceIssue issue1 = createDummyIssue(Map.of(), "vm1");
        InstanceModel model1 = createDummyInstanceModel("", privateKeyPath, "linux", "ubuntu");
        BashFixingPlugin plugin = createPlugin("sudo dir", user, null);

        Assertions.assertThrows(MalformedInstanceModelException.class, () -> plugin.fixIssue(issue1, model1, null));

        InstanceModel model = createDummyInstanceModel("http://this-host-does-not-exit.com", privateKeyPath, "linux", "ubuntu");
        IssueFixingReport report = plugin.fixIssue(issue1, model, null);
        Assertions.assertNotNull(report);
        Assertions.assertFalse(report.isSuccessful());

        model = createDummyInstanceModel(host, privateKeyPath, "linux", "ubuntu");
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME, "drrobot");
        report = plugin.fixIssue(issue1, model, null);
        Assertions.assertNotNull(report);
        Assertions.assertFalse(report.isSuccessful());

        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME, "ubuntu");
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT, "nowaythisisacommand");
        report = plugin.fixIssue(issue1, model, null);
        Assertions.assertNotNull(report);
        Assertions.assertFalse(report.isSuccessful());

        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT, "dir");
        report = plugin.fixIssue(issue1, model, null);
        Assertions.assertNotNull(report);
        Assertions.assertTrue(report.isSuccessful());

        ComplianceIssue issue2 = createDummyIssue(Map.of("param1", "'hello world!'"), "vm1");
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT, "echo");
        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_COMPLIANCE_RULE_ARGUMENTS, "param1");
        report = plugin.fixIssue(issue2, model, null);
        Assertions.assertNotNull(report);
        Assertions.assertTrue(report.isSuccessful());
        Assertions.assertTrue(report.getDescription().contains("echo 'hello world!'"));
        Assertions.assertTrue(report.getDescription().contains("The resulting output was: hello world!"));
    }

    @Test
    void realWorldScenario() throws Exception {
        final String command = "sudo sed -i -e 's/\\s*nullok\\s*/ /g' /etc/pam.d/common-password";
        TestUtils.setupRealWorldScenario(host, user, privateKeyPath);

        try {
            BashFixingPlugin plugin = createPlugin(command, user, null);
            ComplianceIssue issue = createDummyIssue(new HashMap<>(), "vm1");
            InstanceModel instanceModel = createDummyInstanceModel(host, privateKeyPath, "linux", "ubuntu");
            IssueFixingReport report = plugin.fixIssue(issue, instanceModel, null);
            Assertions.assertNotNull(report);
            Assertions.assertTrue(report.isSuccessful());
        } finally {
            TestUtils.teardownRealWorldScenario(host, user, privateKeyPath);
        }
    }

    @Test
    void testFetchPrivateKey() throws IllegalAccessException {
        BashFixingPlugin plugin = new BashFixingPlugin(new BashFixingPluginDescriptor());
        String content = TestUtils.fetchPrivateKey();

        InstanceModel instanceModel = createDummyInstanceModel(null, privateKeyPath, null, null);
        String privateKey = plugin.readPrivateKey((Compute) instanceModel.getDeploymentModel().getComponent("vm1").orElseThrow());
        Assertions.assertNotNull(privateKey);
        Assertions.assertEquals(content, privateKey);

        InstanceModel instanceModel2 = createDummyInstanceModel(null, null, null, null);
        Assertions.assertThrows(MissingConfigurationEntryException.class, () -> plugin.readPrivateKey((Compute) instanceModel2.getDeploymentModel().getComponent("vm1").orElseThrow()));

        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY, "abc");
        Assertions.assertThrows(PrivateKeyNotAccessibleException.class, () -> plugin.readPrivateKey((Compute) instanceModel2.getDeploymentModel().getComponent("vm1").orElseThrow()));

        plugin.setConfigurationEntry(BashFixingPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY, privateKeyPath);
        instanceModel = createDummyInstanceModel(null, null, null, null);
        privateKey = plugin.readPrivateKey((Compute) instanceModel.getDeploymentModel().getComponent("vm1").orElseThrow());
        Assertions.assertNotNull(privateKey);
        Assertions.assertEquals(content, privateKey);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsProvider")
    void testParamValidation(BashFixingPlugin plugin, ComplianceIssue issue, InstanceModel instanceModel, Class<? extends IacmfException> clazz) {
        if (clazz != null) {
            Assertions.assertThrows(clazz, () -> plugin.validateInputs(issue, instanceModel));
        } else {
            Compute compute = plugin.validateInputs(issue, instanceModel);
            Assertions.assertNotNull(compute);
        }
    }
}