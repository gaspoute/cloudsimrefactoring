package com.ulb.test;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class TestedType extends Type {

    public TestedType(String name) {
        super(name);
    }
    
    @Override
    public boolean meets(Resource resource, Resource another) {
        return false;
    }
}
