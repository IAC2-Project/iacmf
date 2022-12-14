package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison;

import io.github.edmm.model.relation.RootRelation;

import java.util.Comparator;

public class RelationComparator implements Comparator<RootRelation> {
    /***
     * Considers that the two relations are compatible, iff they have the exact same type.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 0 if the relations are compatible. -1 otherwise.
     */
    public int compare(RootRelation o1, RootRelation o2) {
        return o1.getClass().equals(o2.getClass()) ? 0 : -1;
    }
}
