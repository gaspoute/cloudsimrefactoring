package com.ulb.simulator;

import com.ulb.utility.Event;

public class TaskSubmissionEvent extends Event {
    private Task task;
    
    public TaskSubmissionEvent(Object source, Object target, Task task) {
        super(source, target);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
