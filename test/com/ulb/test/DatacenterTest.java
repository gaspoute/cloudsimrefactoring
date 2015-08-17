package com.ulb.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Datacenter;
import com.ulb.simulator.InsufficientResourcesException;
import com.ulb.simulator.Server;
import com.ulb.simulator.Specification;
import com.ulb.simulator.VirtualMachine;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class DatacenterTest {
    private Datacenter datacenter;
    private Server server1;
    private Server server2;
    
    @Before
    public void setup() {
        server1 = new Server();
        server1.addResource(new Resource(Type.CPU, 3));
        server1.addResource(new Resource(Type.MEMORY, 1024));
        server2 = new Server();
        server2.addResource(new Resource(Type.CPU, 2));
        server2.addResource(new Resource(Type.MEMORY, 512));
        datacenter = new Datacenter();
        datacenter.addServer(server1);
        datacenter.addServer(server2);
    }
    
    @Test
    public void testMigrateVirtualMachineWithSuccess() throws InsufficientResourcesException {
        Resource resource1 = new Resource(Type.CPU, 2);
        Resource resource2 = new Resource(Type.MEMORY, 512);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        server1.allocateVirtualMachine(virtualMachine);
        Collection<Resource> resources = virtualMachine.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(1, server1.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, server1.getResourceByType(Type.MEMORY).getCapacity());
        datacenter.migrateVirtualMachine(server1.getId(), server2.getId(), virtualMachine.getId());
        resources = virtualMachine.getResources();
        actual = resources.toArray(new Resource[resources.size()]);
        assertEquals(3, server1.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, server1.getResourceByType(Type.MEMORY).getCapacity());
        assertEquals(0, server2.getResourceByType(Type.CPU).getCapacity());
        assertEquals(0, server2.getResourceByType(Type.CPU).getCapacity());
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
    }
    
    @Test(expected=InsufficientResourcesException.class)
    public void testMigrateVirtualMachineWithFailure() throws InsufficientResourcesException {
        Resource resource1 = new Resource(Type.CPU, 3);
        Resource resource2 = new Resource(Type.MEMORY, 1024);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        VirtualMachine virtualMachine = new VirtualMachine(specification);
        server1.allocateVirtualMachine(virtualMachine);
        Collection<Resource> resources = virtualMachine.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(0, server1.getResourceByType(Type.CPU).getCapacity());
        assertEquals(0, server1.getResourceByType(Type.MEMORY).getCapacity());
        datacenter.migrateVirtualMachine(server1.getId(), server2.getId(), virtualMachine.getId());
        resources = virtualMachine.getResources();
        assertEquals(3, server1.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, server1.getResourceByType(Type.MEMORY).getCapacity());
        assertEquals(2, server2.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, server2.getResourceByType(Type.CPU).getCapacity());
        assertTrue(resources.isEmpty());
    }
}
