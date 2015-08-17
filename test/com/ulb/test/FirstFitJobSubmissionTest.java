package com.ulb.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Cloud;
import com.ulb.simulator.Datacenter;
import com.ulb.simulator.FirstFitJobSubmission;
import com.ulb.simulator.Job;
import com.ulb.simulator.JobSubmission;
import com.ulb.simulator.JobSubmissionFailureEvent;
import com.ulb.simulator.JobSubmissionSuccessEvent;
import com.ulb.simulator.Server;
import com.ulb.simulator.Specification;
import com.ulb.simulator.Task;
import com.ulb.simulator.VirtualMachine;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;

public class FirstFitJobSubmissionTest {
    private Cloud cloud;
    
    @Before
    public void setup() throws Exception {
        cloud = new Cloud();
        Datacenter datacenter = new Datacenter();
        Server server = new Server();
        server.addResource(new Resource(Type.CPU, 3));
        server.addResource(new Resource(Type.MEMORY, 1024));
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 2));
        specification.addResource(new Resource(Type.MEMORY, 512));
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        server.allocateVirtualMachine(virtualMachine);
        datacenter.addServer(server);
        cloud.addDatacenter(datacenter);
    }
    
    @Test
    public void testSubmitJobWithSuccess() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(JobSubmissionSuccessEvent.class, null, subscriber);
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 1));
        specification.addResource(new Resource(Type.MEMORY, 256));
        Task task = new Task(specification, 100);
        Job job = new Job();
        job.addTask(task);
        JobSubmission submission = new FirstFitJobSubmission(job, cloud);
        submission.submit();
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        JobSubmissionSuccessEvent event = (JobSubmissionSuccessEvent) events.get(0);
        assertEquals(job, event.getJob());
    }
    
    @Test
    public void testSubmitJobWithFailure() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(JobSubmissionFailureEvent.class, null, subscriber);
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 4));
        specification.addResource(new Resource(Type.MEMORY, 256));
        Task task = new Task(specification, 100);
        Job job = new Job();
        job.addTask(task);
        JobSubmission submission = new FirstFitJobSubmission(job, cloud);
        submission.submit();
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
