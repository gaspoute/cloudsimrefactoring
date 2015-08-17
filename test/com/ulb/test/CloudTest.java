package com.ulb.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Cloud;
import com.ulb.simulator.Datacenter;
import com.ulb.simulator.Job;
import com.ulb.simulator.JobSubmissionFailureEvent;
import com.ulb.simulator.ServiceLevelAgreement;
import com.ulb.simulator.ServiceLevelAgreementNegotiationFailureEvent;
import com.ulb.simulator.Specification;
import com.ulb.simulator.Task;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;

public class CloudTest {
    private Cloud cloud;
    
    @Before
    public void setup() {
        cloud = new Cloud();
        cloud.addDatacenter(new Datacenter());
    }
    
    @Test
    public void testNegotiateServiceLevelAgreement() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(ServiceLevelAgreementNegotiationFailureEvent.class, null, subscriber);
        ServiceLevelAgreement serviceLevelAgreement = new ServiceLevelAgreement();
        cloud.negotiateServiceLevelAgreement(serviceLevelAgreement);
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        ServiceLevelAgreementNegotiationFailureEvent event = (ServiceLevelAgreementNegotiationFailureEvent) events.get(0);
        assertEquals(serviceLevelAgreement, event.getServiceLevelAgreement());
    }
    
    @Test
    public void testSubmitJob() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(JobSubmissionFailureEvent.class, null, subscriber);
        Job job = new Job();
        Task task = new Task(new Specification(), 100);
        job.addTask(task);
        cloud.submitJob(job);
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        JobSubmissionFailureEvent event = (JobSubmissionFailureEvent) events.get(0);
        assertEquals(job, event.getJob());
    }
    
    @After
    public void tearDown() {
        EventService.getInstance().unsubscribeAll();
    }
}
