package org.iac2.common.model.compliancejob.execution;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Execution {
    private Long id;
    private Date startTime;
    private Date endTime;
    private ExecutionStep step;
    private ExecutionStatus status;
}
