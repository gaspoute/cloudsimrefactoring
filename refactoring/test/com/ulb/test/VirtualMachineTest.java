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
import com.ulb.simulator.Task;
import com.ulb.simulator.Task.Status;
import com.ulb.simulator.VirtualMachine;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class VirtualMachineTest {
    private VirtualMachine virtualMachine;
    
    @Before
    public void setup() throws InsufficientResourcesException {
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 3));
        specification.addResource(new Resource(Type.MEMORY, 1024));
        virtualMachine = new VirtualMachine(specification);
        Server server = new Server();
        server.addResource(new Resource(Type.CPU, 3));
        server.addResource(new Resource(Type.MEMORY, 1024));
        server.allocateVirtualMachine(virtualMachine);
    }
    
    @Test
    public void testSubmitTaskWithSatisfaction() throws InsufficientResourcesException {
        Resource resource1 = new Resource(Type.CPU, 2);
        Resource resource2 = new Resource(Type.MEMORY, 512);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        Task task = new Task(specification, 100);
        virtualMachine.submitTask(task);
        Collection<Resource> resources = task.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(1, virtualMachine.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, virtualMachine.getResourceByType(Type.MEMORY).getCapacity());
    }
    
    @Test(expected=InsufficientResourcesException.class)
    public void testSubmitTaskWithDissatisfaction() throws InsufficientResourcesException {
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 3));
        specification.addResource(new Resource(Type.MEMORY, 2048));
        Task task = new Task(specification, 100);
        virtualMachine.submitTask(task);
        Collection<Resource> resources = task.getResources();
        assertTrue(resources.isEmpty());
        assertEquals(3, virtualMachine.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, virtualMachine.getResourceByType(Type.MEMORY).getCapacity());
    }
    
    @Test
    public void testCancelTask() throws InsufficientResourcesException {
        Task task = new Task(new Specification(), 100);
        virtualMachine.submitTask(task);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        virtualMachine.cancelTask(task.getId());
        assertEquals(Status.CANCELED, task.getStatus());
        virtualMachine.cancelTask(task.getId());
        assertEquals(Status.CANCELED, task.getStatus());
    }
    
    @Test
    public void testPauseTask() throws InsufficientResourcesException {
        Task task = new Task(new Specification(), 100);
        virtualMachine.submitTask(task);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        virtualMachine.pauseTask(task.getId());
        assertEquals(Status.PAUSED, task.getStatus());
        virtualMachine.pauseTask(task.getId());
        assertEquals(Status.PAUSED, task.getStatus());
    }
    
    @Test
    public void testResumeTask() throws InsufficientResourcesException {
        Task task = new Task(new Specification(), 100);
        virtualMachine.submitTask(task);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        virtualMachine.pauseTask(task.getId());
        assertEquals(Status.PAUSED, task.getStatus());
        virtualMachine.resumeTask(task.getId());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        virtualMachine.resumeTask(task.getId());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }
}
