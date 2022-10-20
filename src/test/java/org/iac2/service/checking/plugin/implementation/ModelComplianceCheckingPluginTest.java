package org.iac2.service.checking.plugin.implementation;

import com.google.common.collect.Maps;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.assertj.core.util.Sets;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.compliancerule.parameter.ComplianceRuleParameter;
import org.iac2.common.model.compliancerule.parameter.StringComplianceRuleParameter;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.plugin.manager.implementation.SimpleCRCheckingManager;
import org.iac2.util.OpenTOSCATestUtils;
import org.iac2.util.TestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelComplianceCheckingPluginTest {

    private static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-example-applications";
    private static final QName csarId = new QName("http://opentosca.org/example/applications/servicetemplates", "RealWorld-Application_Angular-Spring-MySQL-w1");
    private static final String hostName = "localhost";
    private static final String port = "1337";
    private static Path csarPath;
    private static String appName = "RealWorld-Application_Angular-Spring-MySQL-w1";
    private static String instanceId = "";
    private static ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).withTimeout(20, TimeUnit.MINUTES).build();

    // set this to true if you want faster execution of this test when you probably need to run it more often
    private static boolean debugging = true;


    @BeforeAll
    public static void setupContainer() throws GitAPIException, AccountabilityException, RepositoryCorruptException, IOException, ExecutionException, InterruptedException {
        csarPath = TestUtils.fetchCsar(TESTAPPLICATIONSREPOSITORY, csarId);
        appName = csarPath.getFileName().toString();
        OpenTOSCATestUtils.uploadApp(client, appName, csarPath);
        instanceId = OpenTOSCATestUtils.provisionApp(client, appName);
    }

    @AfterAll
    public static void cleanupContainer() {
        if (!debugging) {
            OpenTOSCATestUtils.terminateApp(client, appName, hostName, port);
            client.getApplications().forEach(a -> client.removeApplication(a));
        }
    }

    private InstanceModel reconstructeModel(ProductionSystem productionSystem) {
        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelCreationPlugin plugin = instance.getModelCreationPlugin("opentosca-container-model-creation-plugin");
        assertNotNull(plugin);
        assertEquals("opentosca-container-model-creation-plugin", plugin.getIdentifier());

        InstanceModel instanceModel = plugin.reconstructInstanceModel(productionSystem);
        return instanceModel;
    }

    private InstanceModel enhanceModel(ProductionSystem productionSystem, InstanceModel instanceModel) {
        Set<RootComponent> comps = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> rels = instanceModel.getDeploymentModel().getRelations();

        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelEnhancementPlugin enhancementPlugin = instance.getModelEnhancementPlugin("docker-enhancement-plugin");
        InstanceModel instanceModel1 = enhancementPlugin.enhanceModel(instanceModel, productionSystem);
        return instanceModel1;
    }

    @Test
    public void checkForModelCompliance() {
        ProductionSystem productionSystem = OpenTOSCATestUtils.createProductionSystem(this.hostName, this.port, this.appName, this.instanceId);
        InstanceModel instanceModel = this.reconstructeModel(productionSystem);
        instanceModel = this.enhanceModel(productionSystem, instanceModel);

        SimpleCRCheckingManager manager = SimpleCRCheckingManager.getInstance();
        ComplianceRuleCheckingPlugin plugin = manager.getPlugin("opentosca-modelcompliance-checking-plugin");
        ComplianceRule rule = new ComplianceRule();
        rule.setType("modelCompliance");
        rule.setLocation("");

        Collection<ComplianceRuleParameter> complianceRuleParameters = Sets.newHashSet();
        ComplianceRuleParameter iacToolParameter = new StringComplianceRuleParameter("iacToolUrl", "http://localhost:1337");
        ComplianceRuleParameter instanceIdParameter = new StringComplianceRuleParameter("instanceId", instanceId);
        ComplianceRuleParameter appIdParameter = new StringComplianceRuleParameter("appId", csarPath.getFileName().toString());
        complianceRuleParameters.add(iacToolParameter);
        complianceRuleParameters.add(instanceIdParameter);
        complianceRuleParameters.add(appIdParameter);
        rule.setParameterAssignments(complianceRuleParameters);

        Collection<ComplianceIssue> issues = plugin.findIssues(instanceModel, rule);
        Assert.assertNotEquals(0, issues.size());
    }
}
