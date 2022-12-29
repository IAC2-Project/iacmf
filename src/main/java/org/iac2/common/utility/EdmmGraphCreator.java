package org.iac2.common.utility;

import java.util.Set;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;

public class EdmmGraphCreator {

    public static Graph<RootComponent, RootRelation> of(EntityGraph entityGraph) {
        DeploymentModel deploymentModel = new DeploymentModel("dummy", entityGraph);

        return of(deploymentModel);
    }

    public static Graph<RootComponent, RootRelation> of(DeploymentModel deploymentModel) {
        Graph<RootComponent, RootRelation> graph = deploymentModel.getTopology();
        SimpleDirectedGraph<RootComponent, RootRelation> result = new SimpleDirectedGraph<>(RootRelation.class);
        graph.vertexSet().forEach(result::addVertex);
        Set<RootRelation> edges = graph.edgeSet();

        for (RootRelation edge : edges) {
            result.addEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), edge);
        }

        return result;
    }
}
