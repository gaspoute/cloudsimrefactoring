package com.ulb.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.InsufficientResourcesException;
import com.ulb.simulator.Server;
import com.ulb.simulator.Specification;
import com.ulb.simulator.VirtualMachine;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class ServerTest {
    private Server server;
    
    @Before
    public void setup() {
        server = new Server();
        server.addResource(new Resource(Type.CPU, 3));
        server.addResource(new Resource(Type.MEMORY, 1024));
    }
    
    @Test
    public void testAllocateVirtualMachineWithSatisfaction() throws InsufficientResourcesException {
        Resource resource1 = new Resource(Type.CPU, 2);
        Resource resource2 = new Resource(Type.MEMORY, 512);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        server.allocateVirtualMachine(virtualMachine);
        Collection<Resource> resources = virtualMachine.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(1, server.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, server.getResourceByType(Type.MEMORY).getCapacity());
    }
    
    @Test(expected=InsufficientResourcesException.class)
    public void testAllocateVirtualMachineWithDissatisfaction() throws InsufficientResourcesException {
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 3));
        specification.addResource(new Resource(Type.MEMORY, 2048));
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        server.allocateVirtualMachine(virtualMachine);
        Collection<Resource> resources = virtualMachine.getResources();
        assertTrue(resources.isEmpty());
        assertEquals(3, server.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, server.getResourceByType(Type.MEMORY).getCapacity());
    }
    
    @Test
    public void testDeallocateVirtualMachine() throws InsufficientResourcesException {
        Resource resource1 = new Resource(Type.CPU, 2);
        Resource resource2 = new Resource(Type.MEMORY, 512);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        server.allocateVirtualMachine(virtualMachine);
        Collection<Resource> resources = virtualMachine.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(1, server.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, server.getResourceByType(Type.MEMORY).getCapacity());
        VirtualMachine deallocatedVirtualMachine = server.deallocateVirtualMachine(virtualMachine.getId());
        resources = deallocatedVirtualMachine.getResources();
        assertEquals(deallocatedVirtualMachine, virtualMachine);
        assertTrue(resources.isEmpty());
        assertEquals(3, server.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, server.getResourceByType(Type.MEMORY).getCapacity());
    }
}
