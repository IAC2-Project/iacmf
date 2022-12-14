package org.iac2.service.architecturereconstruction.common.interfaces;

import java.util.Collection;

import org.iac2.common.Plugin;

public interface ArchitectureReconstructionPlugin extends Plugin {
    Collection<String> getRequiredProductionSystemPropertyNames();
}
