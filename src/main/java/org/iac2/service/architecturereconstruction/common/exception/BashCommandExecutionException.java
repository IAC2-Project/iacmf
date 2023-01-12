package org.iac2.service.architecturereconstruction.common.exception;

import lombok.Getter;

@Getter
public class BashCommandExecutionException extends ArchitectureReconstructionException {
    private String command;
    private String componentId;
    private String host;
    private String userName;

    public BashCommandExecutionException(String command, String componentId, String host, String userName) {
        super("An error occurred while trying to execute the command (%s) over ssh (%s@%s)".formatted(command, userName, host));
        this.command = command;
        this.componentId = componentId;
        this.host = host;
        this.userName = userName;
    }
}
