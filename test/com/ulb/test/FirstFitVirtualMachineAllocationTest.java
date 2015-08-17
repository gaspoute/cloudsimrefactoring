package com.ulb.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Cloud;
import com.ulb.simulator.Datacenter;
import com.ulb.simulator.FirstFitVirtualMachineAllocation;
import com.ulb.simulator.Server;
import com.ulb.simulator.Specification;
import com.ulb.simulator.VirtualMachine;
import com.ulb.simulator.VirtualMachineAllocation;
import com.ulb.simulator.VirtualMachineAllocationFailureEvent;
import com.ulb.simulator.VirtualMachineAllocationSuccessEvent;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;

public class FirstFitVirtualMachineAllocationTest {
    private Cloud cloud;
    
    @Before
    public void setup() {
        cloud = new Cloud();
        Datacenter datacenter = new Datacenter();
        Server server = new Server();
        server.addResource(new Resource(Type.CPU, 1));
        server.addResource(new Resource(Type.MEMORY, 1024));
        datacenter.addServer(server);
        cloud.addDatacenter(datacenter);
        datacenter = new Datacenter();
        server.addResource(new Resource(Type.CPU, 2));
        server.addResource(new Resource(Type.MEMORY, 512));
        datacenter.addServer(server);
        server = new Server();
        server.addResource(new Resource(Type.CPU, 4));
        server.addResource(new Resource(Type.MEMORY, 2048));
        datacenter.addServer(server);
        cloud.addDatacenter(datacenter);
    }
    
    @Test
    public void testAllocateWithSuccess() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(VirtualMachineAllocationSuccessEvent.class, null, subscriber);
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 4));
        specification.addResource(new Resource(Type.MEMORY, 2048));
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        VirtualMachineAllocation allocation = new FirstFitVirtualMachineAllocation(virtualMachine, cloud);
        allocation.allocate();
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        VirtualMachineAllocationSuccessEvent event = (VirtualMachineAllocationSuccessEvent) events.get(0);
        assertEquals(virtualMachine, event.getVirtualMachine());
    }
    
    @Test
    public void testAllocateWithFailure() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(VirtualMachineAllocationFailureEvent.class, null, subscriber);
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 5));
        specification.addResource(new Resource(Type.MEMORY, 512));
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        VirtualMachineAllocation allocation = new FirstFitVirtualMachineAllocation(virtualMachine, cloud);
        allocation.allocate();
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(1, events.size());
        VirtualMachineAllocationFailureEvent event = (VirtualMachineAllocationFailureEvent) events.get(0);
        assertEquals(virtualMachine, event.getVirtualMachine());
    }
    
    @Test
    public void testAllocationWithFailure2() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(VirtualMachineAllocationSuccessEvent.class, null, subscriber);
        EventService.getInstance().subscribe(VirtualMachineAllocationFailureEvent.class, null, subscriber);
        Specification specification1 = new Specification();
        specification1.addResource(new Resource(Type.CPU, 2));
        specification1.addResource(new Resource(Type.MEMORY, 512));
        VirtualMachine virtualMachine1 = new VirtualMachine(specification1);
        Specification specification2 = new Specification();
        specification2.addResource(new Resource(Type.CPU, 2));
        specification2.addResource(new Resource(Type.MEMORY, 512));
        VirtualMachine virtualMachine2 = new VirtualMachine(specification2);
        Specification specification3 = new Specification();
        specification3.addResource(new Resource(Type.CPU, 2));
        specification3.addResource(new Resource(Type.MEMORY, 512));
        VirtualMachine virtualMachine3 = new VirtualMachine(specification3);
        VirtualMachineAllocation allocation1 = new FirstFitVirtualMachineAllocation(virtualMachine1, cloud);
        allocation1.allocate();
        VirtualMachineAllocation allocation2 = new FirstFitVirtualMachineAllocation(virtualMachine2, cloud);
        allocation2.allocate();
        VirtualMachineAllocation allocation3 = new FirstFitVirtualMachineAllocation(virtualMachine3, cloud);
        allocation3.allocate();
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertEquals(3, events.size());
        VirtualMachineAllocationSuccessEvent event1 = (VirtualMachineAllocationSuccessEvent) events.get(0);
        assertEquals(virtualMachine1, event1.getVirtualMachine());
        VirtualMachineAllocationFailureEvent event2 = (VirtualMachineAllocationFailureEvent) events.get(1);
        assertEquals(virtualMachine2, event2.getVirtualMachine());
        VirtualMachineAllocationFailureEvent event3 = (VirtualMachineAllocationFailureEvent) events.get(2);
        assertEquals(virtualMachine3, event3.getVirtualMachine());
    }
        
    @After
    public void tearDown() {
        EventService.getInstance().unsubscribeAll();
    }
}
