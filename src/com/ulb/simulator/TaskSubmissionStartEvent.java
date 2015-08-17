package com.ulb.simulator;

public class TaskSubmissionStartEvent extends TaskSubmissionEvent {

    public TaskSubmissionStartEvent(Object source, Object target, Task task) {
        super(source, target, task);
    }
}
