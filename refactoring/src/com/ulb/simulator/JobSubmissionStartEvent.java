package com.ulb.simulator;

public class JobSubmissionStartEvent extends JobSubmissionEvent {

    public JobSubmissionStartEvent(Object source, Object target, Job job) {
        super(source, target, job);
    }
}
