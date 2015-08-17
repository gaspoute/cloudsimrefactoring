package com.ulb.simulator;

import java.util.ArrayList;
import java.util.List;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.Subscriber;
import com.ulb.utility.TargetFilter;

public class Cloud implements Subscriber {
    private List<Datacenter> datacenters = new ArrayList<Datacenter>();
    
    public Cloud() {
        EventService.getInstance().subscribe(ServiceLevelAgreementNegotiationStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(JobSubmissionStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(JobSubmissionSuccessEvent.class, null, this);
    }
    
    public void addDatacenter(Datacenter datacenter) {
        datacenters.add(datacenter);
    }

    public void negotiateServiceLevelAgreement(ServiceLevelAgreement serviceLevelAgreement) {
        ServiceLevelAgreementNegotiation negotiation = new SimpleServiceLevelAgreementNegotiation(serviceLevelAgreement, this);
        negotiation.negotiate();
    }
    
    public void allocateVirtualMachine(VirtualMachine virtualMachine)  {
        VirtualMachineAllocation allocation = new FirstFitVirtualMachineAllocation(virtualMachine, this);
        allocation.allocate();
    }

    public void submitJob(Job job) {
        JobSubmission submission = new FirstFitJobSubmission(job, this);
        submission.submit();
    }
    
    public void run(long currentTime) {
        for (Datacenter datacenter : datacenters) {
            EventService.getInstance().publishEvent(new RunningEvent(this, datacenter, currentTime));
        }
    }

    @Override
    public void inform(Event event) {
        if (event instanceof ServiceLevelAgreementNegotiationStartEvent) {
            ServiceLevelAgreementNegotiationStartEvent signal = (ServiceLevelAgreementNegotiationStartEvent) event;
            negotiateServiceLevelAgreement(signal.getServiceLevelAgreement());
        } else if (event instanceof JobSubmissionStartEvent) {
            JobSubmissionStartEvent signal = (JobSubmissionStartEvent) event;
            submitJob(signal.getJob());
        } else if (event instanceof JobSubmissionSuccessEvent) {
            run(event.getWhen());
        }
    }

    public List<Datacenter> getDatacenters() {
        return datacenters;
    }
}