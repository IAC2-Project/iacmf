package org.iac2.service.architecturereconstruction.common.interfaces;

import java.util.Collection;

import org.iac2.common.PluginDescriptor;

public interface ArchitectureReconstructionPluginDescriptor extends PluginDescriptor {
    Collection<String> getRequiredProductionSystemPropertyNames();
}
