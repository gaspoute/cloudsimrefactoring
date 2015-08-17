package com.ulb.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class TypeTest {
    private Type cpu;
    private Type memory;
    private Type storage;
    
    @Before
    public void setup() {
        cpu = Type.CPU;
        memory = Type.MEMORY;
        storage = Type.STORAGE;
    }
    
    @Test
    public void testEquals() {
        Type type1 = new TestedType(null);
        Type type2 = new TestedType(null);
        assertTrue(cpu.equals(cpu));
        assertTrue(cpu.equals(Type.CPU));
        assertFalse(cpu.equals(null));
        assertFalse(cpu.equals(new Object()));
        assertFalse(cpu.equals(Type.MEMORY));
        assertFalse(cpu.equals(type1));
        assertTrue(type1.equals(type2) && type2.equals(type1));
        assertTrue(type1.hashCode() == type2.hashCode());
    }
    
    @Test
    public void testMeets() {
        assertTrue(cpu.meets(new Resource(cpu, 3), new Resource(Type.CPU, 2)));
        assertFalse(cpu.meets(new Resource(cpu, 3), new Resource(Type.CPU, 4)));
        assertTrue(memory.meets(new Resource(memory, 1024), new Resource(Type.MEMORY, 512)));
        assertFalse(memory.meets(new Resource(memory, 1024), new Resource(Type.CPU, 2048)));
        assertTrue(storage.meets(new Resource(storage, 1024), new Resource(Type.MEMORY, 512)));
        assertFalse(storage.meets(new Resource(storage, 1024), new Resource(Type.CPU, 2048)));
    }
}
