package com.ulb.simulator;

import java.util.ArrayList;
import java.util.List;

import com.ulb.simulator.resource.Resource;

public class Specification {
    private List<Resource> resources = new ArrayList<Resource>();
    
    public void addResource(Resource resource) {
        resources.add(resource);
    }
    
    public boolean isSatisfiedBy(Provider provider) {
        for (Resource resource : resources) {
            if (!provider.satisfies(resource)) {
                return false;
            }
        }
        return true;
    }

    public void appliedBy(Provider provider, Consumer consumer) {
        for (Resource resource : resources) {
            provider.offer(resource);
            consumer.addResource(resource);
        }
    }
}
