package org.iac2.service.architecturereconstruction.common.interfaces;

public interface ModelCreationPluginDescriptor extends ArchitectureReconstructionPluginDescriptor {
    boolean isIaCTechnologySupported(String iacTechnologyName);
}
