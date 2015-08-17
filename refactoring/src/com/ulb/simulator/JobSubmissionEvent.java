package com.ulb.simulator;

import com.ulb.utility.Event;

public class JobSubmissionEvent extends Event {
    private Job job;

    public JobSubmissionEvent(Object source, Job job) {
        super(source);
        this.job = job;
    }
    
    public JobSubmissionEvent(Object source, Object target, Job job) {
        super(source, target);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
