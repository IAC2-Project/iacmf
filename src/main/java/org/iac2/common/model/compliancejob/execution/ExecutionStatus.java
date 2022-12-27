package org.iac2.common.model.compliancejob.execution;

public enum ExecutionStatus {
    /***
     * The execution has been created but not yet started.
     */
    CREATED,
    /***
     * One of the execution steps is currently running.
     */
    RUNNING,
    /***
     * The execution is in between two steps.
     */
    IDLE,
    /***
     * The execution has finished without errors (internal or bad user inputs).
     */
    SUCCESS,
    /***
     * The execution has finished because of an internal or a user-input error.
     */
    EXCEPTION
}
