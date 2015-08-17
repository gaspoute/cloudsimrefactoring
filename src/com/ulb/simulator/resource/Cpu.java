package com.ulb.simulator.resource;

public class Cpu extends Type {
    
    public Cpu() {
        super("Cpu");
    }
    
    @Override
    public boolean meets(Resource resource, Resource another) {
        return resource.getCapacity() >= another.getCapacity();
    }
}
