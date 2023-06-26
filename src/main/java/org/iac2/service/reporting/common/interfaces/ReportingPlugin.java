package org.iac2.service.reporting.common.interfaces;

import org.iac2.common.Plugin;
import org.iac2.service.reporting.common.model.ExecutionReport;

public interface ReportingPlugin extends Plugin {

    void reportExecutionOutcome(ExecutionReport report);
}
