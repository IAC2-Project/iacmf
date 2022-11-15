package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class DockerContainer extends SoftwareComponent {
    public static final Attribute<String> STATE = new Attribute<>("State", String.class);
    public static final Attribute<String> CONTAINER_ID = new Attribute<>("ContainerID", String.class);
    public static final Attribute<String> PORT = new Attribute<>("Port", String.class);
    public static final Attribute<String> CONTAINER_PORT = new Attribute<>("ContainerPort", String.class);
    public static final Attribute<String> IMAGE_ID = new Attribute<>("ImageId", String.class);
    public static final Attribute<String> CONTAINER_MOUNT_PATH = new Attribute<>("ContainerMountPath", String.class);
    public static final Attribute<String> HOST_MOUNT_FILES = new Attribute<>("HostMountFiles", String.class);
    public static final Attribute<String> PRIVILEGED_Mode = new Attribute<>("PrivilegedMode", String.class);

    public DockerContainer(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "DockerContainer()";
    }
}
