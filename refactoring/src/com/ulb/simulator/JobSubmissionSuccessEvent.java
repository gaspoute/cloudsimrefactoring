package com.ulb.simulator;

public class JobSubmissionSuccessEvent extends JobSubmissionEvent {
    
    public JobSubmissionSuccessEvent(Object source, Job job) {
        super(source, job);
    }
}
