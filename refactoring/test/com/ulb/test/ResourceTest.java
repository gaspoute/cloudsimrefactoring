package com.ulb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class ResourceTest {
    private Resource resource;
    
    @Before
    public void setup() {
        resource = new Resource(Type.MEMORY, 1024);
    }
    
    @Test
    public void testEquals() {
        Resource resource1 = new Resource(null, 0);
        Resource resource2 = new Resource(null, 0);
        Resource resource3 = new Resource(Type.MEMORY, 1024);
        assertTrue(resource.equals(resource));
        assertTrue(resource.equals(new Resource(Type.MEMORY, 1024)));
        assertFalse(resource.equals(null));
        assertFalse(resource.equals(new Object()));
        assertFalse(resource.equals(new Resource(Type.MEMORY, 512)));
        assertFalse(resource.equals(new Resource(Type.CPU, 3)));
        assertFalse(resource1.equals(resource));
        assertTrue(resource.equals(resource3) && resource3.equals(resource));
        assertTrue(resource.hashCode() == resource3.hashCode());
        assertTrue(resource1.equals(resource2) && resource2.equals(resource1));
        assertTrue(resource1.hashCode() == resource2.hashCode());
    }
    
    @Test
    public void testMeets() {
        assertTrue(resource.meets(resource));
        assertTrue(resource.meets(new Resource(Type.MEMORY, 1024)));
        assertTrue(resource.meets(new Resource(Type.MEMORY, 512)));
        assertFalse(resource.meets(new Resource(Type.MEMORY, 2048)));
        assertFalse(resource.meets(new Resource(Type.CPU, 3)));
    }
    
    @Test
    public void testMinus() {
        resource.minus(new Resource(Type.MEMORY, 512));
        assertEquals(512, resource.getCapacity());
        resource.minus(new Resource(Type.CPU, 2));
        assertEquals(512, resource.getCapacity());
    }
    
    @Test
    public void testPlus() {
        resource.plus(new Resource(Type.MEMORY, 1024));
        assertEquals(2048, resource.getCapacity());
        resource.plus(new Resource(Type.CPU, 2));
        assertEquals(2048, resource.getCapacity());
    }
}