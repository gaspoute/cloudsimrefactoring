package com.ulb.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Broker;
import com.ulb.simulator.Cloud;
import com.ulb.simulator.Datacenter;
import com.ulb.simulator.Job;
import com.ulb.simulator.JobSubmissionSuccessEvent;
import com.ulb.simulator.Server;
import com.ulb.simulator.ServiceLevelAgreement;
import com.ulb.simulator.Simulation;
import com.ulb.simulator.Specification;
import com.ulb.simulator.Task;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;

public class BrokerTest {
    private Cloud cloud;
    private Broker broker;
    private Simulation simulation;
    
    @Before
    public void setup() {
        Datacenter datacenter = new Datacenter();
        Server server = new Server();
        server.addResource(new Resource(Type.CPU, 3));
        server.addResource(new Resource(Type.MEMORY, 1024));
        datacenter.addServer(server);
        cloud = new Cloud();
        cloud.addDatacenter(datacenter);
        broker = new Broker(new ServiceLevelAgreement(), cloud);
        simulation = new Simulation(broker);
    }
    
    @Test
    public void test() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(JobSubmissionSuccessEvent.class, null, subscriber);
        Job job = new Job();
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 1));
        specification.addResource(new Resource(Type.MEMORY, 245));
        job.addTask(new Task(specification, 100));
        broker.addJob(job);
        simulation.run();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        JobSubmissionSuccessEvent event = (JobSubmissionSuccessEvent) events.get(0);
        assertEquals(job, event.getJob());
    }
    
    @After
    public void tearDown() {
        EventService.getInstance().unsubscribeAll();
    }
}
