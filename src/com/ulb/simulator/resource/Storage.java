package com.ulb.simulator.resource;

public class Storage extends Type {

    public Storage() {
        super("Storage");
    }
    
    @Override
    public boolean meets(Resource resource, Resource another) {
        return resource.getCapacity() >= another.getCapacity();
    }
}
