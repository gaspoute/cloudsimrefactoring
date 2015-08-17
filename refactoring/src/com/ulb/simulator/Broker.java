package com.ulb.simulator;

import java.util.ArrayList;
import java.util.List;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.Subscriber;

public class Broker implements Subscriber {
    private ServiceLevelAgreement serviceLevelAgreement;
    private Cloud cloud;
    private List<Job> jobs = new ArrayList<Job>();

    public Broker(ServiceLevelAgreement serviceLevelAgreement, Cloud cloud) {
        this.serviceLevelAgreement = serviceLevelAgreement;
        this.cloud = cloud;
        EventService.getInstance().subscribe(SimulationStartEvent.class, null, this);
        EventService.getInstance().subscribe(ServiceLevelAgreementNegotiationSuccessEvent.class, null, this);
    }

    public void addJob(Job job) {
        jobs.add(job);
    }
    
    @Override
    public void inform(Event event) {
        if (event instanceof SimulationStartEvent) {
            EventService.getInstance().publishEvent(new ServiceLevelAgreementNegotiationStartEvent(this, cloud, serviceLevelAgreement));
        } else if (event instanceof ServiceLevelAgreementNegotiationSuccessEvent) {
            for (Job job : jobs) {
                EventService.getInstance().publishEvent(new JobSubmissionStartEvent(this, cloud, job));
            }
        }
    }
}