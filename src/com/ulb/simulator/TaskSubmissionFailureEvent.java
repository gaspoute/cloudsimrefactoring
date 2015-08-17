package com.ulb.simulator;

public class TaskSubmissionFailureEvent extends TaskSubmissionEvent {
    
    public TaskSubmissionFailureEvent(Object source, Object target, Task task) {
        super(source, target, task);
    }
}
