package org.iac2.architecturereconstruction.common.interfaces;

import java.util.Collection;

public interface ArchitectureReconstructionPlugin {
    Collection<String> getRequiredPropertyNames();

    String getIdentifier();
}
