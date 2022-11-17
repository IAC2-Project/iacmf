package org.iac2.service.checking.plugin.implementation.subraphmatching;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.jgrapht.Graph;

public class SubgraphMatchingCheckingPlugin implements ComplianceRuleCheckingPlugin {

    public static final String PLUGIN_ID = "subgraph-matching-checking-plugin";

    @Override
    public Set<String> requiredConfiguration() {
        return new HashSet<>();
    }

    @Override
    public void setConfiguration(String key, String value) {
        if(!requiredConfiguration().contains(key)) {
            throw new IllegalArgumentException("The configuration key '" + "' is not expected.");
        }
    }

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("subgraph-matching");
    }

    @Override
    public String getIdentifier() {
        return PLUGIN_ID;
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) {
        final String ruleLocation = rule.getLocation();
        final String requiredStructureSegment = "/requiredstructure/edmm/export?edmmUseAbsolutePaths=true";
        final String identifierSegment = "/identifier/edmm/export?edmmUseAbsolutePaths=true";

        try {
            Graph<RootComponent,RootRelation> requiredStructure = getRulePart(ruleLocation.concat(requiredStructureSegment));
            Graph<RootComponent,RootRelation> identifier = getRulePart(ruleLocation.concat(identifierSegment));
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }


    public Graph<RootComponent, RootRelation> getRulePart(String fullUrl) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(fullUrl))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String edmmModel = response.body();
        DeploymentModel model = DeploymentModel.of(edmmModel);


        return model.getTopology();
    }
}
