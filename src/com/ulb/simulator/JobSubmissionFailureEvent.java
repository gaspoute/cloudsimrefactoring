package com.ulb.simulator;

public class JobSubmissionFailureEvent extends JobSubmissionEvent {
    
    public JobSubmissionFailureEvent(Object source, Job job) {
        super(source, job);
    }
}
