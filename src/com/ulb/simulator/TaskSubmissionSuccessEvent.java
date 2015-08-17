package com.ulb.simulator;

public class TaskSubmissionSuccessEvent extends TaskSubmissionEvent {
    
    public TaskSubmissionSuccessEvent(Object source, Object target, Task task) {
        super(source, target, task);
    }
}
