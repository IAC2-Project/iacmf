package org.iac2.service.architecturereconstruction.common.interfaces;

import java.util.Collection;

public interface ArchitectureReconstructionPlugin {
    Collection<String> getRequiredPropertyNames();

    String getIdentifier();
}
