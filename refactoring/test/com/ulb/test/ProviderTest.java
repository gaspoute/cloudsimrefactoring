package com.ulb.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.Consumer;
import com.ulb.simulator.DefaultProvider;
import com.ulb.simulator.InsufficientResourcesException;
import com.ulb.simulator.Provider;
import com.ulb.simulator.Specification;
import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class ProviderTest {
    private Provider provider;
    
    @Before
    public void setup() {
        provider = new DefaultProvider();
        provider.addResource(new Resource(Type.CPU, 3));
        provider.addResource(new Resource(Type.MEMORY, 1024));
    }
    
    @Test
    public void testProvideWithSatisfaction() throws Exception {
        Resource resource1 = new Resource(Type.CPU, 2);
        Resource resource2 = new Resource(Type.MEMORY, 512);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        Consumer consumer = new Consumer(specification);
        provider.provide(consumer);
        Collection<Resource> resources = consumer.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(1, provider.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, provider.getResourceByType(Type.MEMORY).getCapacity());
    }
    
    @Test(expected=InsufficientResourcesException.class)
    public void testProvideWithDissatisfaction() throws InsufficientResourcesException {
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, 3));
        specification.addResource(new Resource(Type.MEMORY, 2048));
        Consumer consumer = new Consumer(specification);
        provider.provide(consumer);
        Collection<Resource> resources = consumer.getResources();
        assertTrue(resources.isEmpty());
        assertEquals(3, provider.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, provider.getResourceByType(Type.MEMORY).getCapacity());
    }
    
    @Test
    public void testConsume() throws InsufficientResourcesException {
        Resource resource1 = new Resource(Type.CPU, 2);
        Resource resource2 = new Resource(Type.MEMORY, 512);
        Specification specification = new Specification();
        specification.addResource(resource1);
        specification.addResource(resource2);
        Consumer consumer = new Consumer(specification);
        provider.provide(consumer);
        Collection<Resource> resources = consumer.getResources();
        Resource[] actual = resources.toArray(new Resource[resources.size()]);
        assertArrayEquals(new Resource[] {resource1, resource2}, actual);
        assertEquals(1, provider.getResourceByType(Type.CPU).getCapacity());
        assertEquals(512, provider.getResourceByType(Type.MEMORY).getCapacity());
        provider.consume(consumer);
        resources = consumer.getResources();
        assertTrue(resources.isEmpty());
        assertEquals(3, provider.getResourceByType(Type.CPU).getCapacity());
        assertEquals(1024, provider.getResourceByType(Type.MEMORY).getCapacity());
    }
}
