package com.ulb.simulator;

import com.ulb.utility.Event;

public class InterrogationAboutTaskEvent extends Event {
    private Task task;
    
    public InterrogationAboutTaskEvent(Object source, Object target, Task task) {
        super(source, target);
        this.task = task;
    }
    
    public Task getTask() {
        return task;
    }
}
