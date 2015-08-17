package com.ulb.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Cloud;
import com.ulb.simulator.Datacenter;
import com.ulb.simulator.Server;
import com.ulb.simulator.ServiceLevelAgreement;
import com.ulb.simulator.ServiceLevelAgreementNegotiation;
import com.ulb.simulator.ServiceLevelAgreementNegotiationFailureEvent;
import com.ulb.simulator.ServiceLevelAgreementNegotiationSuccessEvent;
import com.ulb.simulator.SimpleServiceLevelAgreementNegotiation;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;

public class SimpleServiceLevelAgreementNegotiationTest {
    private Cloud cloud;
    private Datacenter datacenter;
    
    @Before
    public void setup() {
        datacenter = new Datacenter();
        cloud = new Cloud();
        cloud.addDatacenter(datacenter);
    }
    
    @Test
    public void testNegotiateWithSuccess() {
        Server server = new Server();
        server.addResource(new Resource(Type.CPU, 3));
        server.addResource(new Resource(Type.MEMORY, 1024));
        datacenter.addServer(server);
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(ServiceLevelAgreementNegotiationSuccessEvent.class, null, subscriber);
        ServiceLevelAgreement serviceLevelAgreement = new ServiceLevelAgreement();
        ServiceLevelAgreementNegotiation negotiation = new SimpleServiceLevelAgreementNegotiation(serviceLevelAgreement, cloud);
        negotiation.negotiate();
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        ServiceLevelAgreementNegotiationSuccessEvent event = (ServiceLevelAgreementNegotiationSuccessEvent) events.get(0);
        assertEquals(serviceLevelAgreement, event.getServiceLevelAgreement());
    }
    
    @Test
    public void testNegotiateWithFailure() {
        Server server = new Server();
        server.addResource(new Resource(Type.CPU, 2));
        server.addResource(new Resource(Type.MEMORY, 1024));
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(ServiceLevelAgreementNegotiationFailureEvent.class, null, subscriber);
        ServiceLevelAgreement serviceLevelAgreement = new ServiceLevelAgreement();
        ServiceLevelAgreementNegotiation negotiation = new SimpleServiceLevelAgreementNegotiation(serviceLevelAgreement, cloud);
        negotiation.negotiate();
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        ServiceLevelAgreementNegotiationFailureEvent event = (ServiceLevelAgreementNegotiationFailureEvent) events.get(0);
        assertEquals(serviceLevelAgreement, event.getServiceLevelAgreement());
    }
    
    @After
    public void tearDown() {
        EventService.getInstance().unsubscribeAll();
    }
}
