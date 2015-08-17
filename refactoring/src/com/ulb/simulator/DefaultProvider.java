package com.ulb.simulator;

import java.util.HashMap;
import java.util.Map;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class DefaultProvider implements Provider {
    private Map<Type, Resource> resources = new HashMap<Type, Resource>();

    @Override
    public void addResource(Resource resource) {
        resources.put(resource.getType(), resource);
    }

    @Override
    public boolean satisfies(Resource anotherResource) {
        Resource resource = getResourceByType(anotherResource.getType());
        return resources.containsKey(anotherResource.getType()) && resource.meets(anotherResource);
    }

    @Override
    public void offer(Resource anotherResource) {
        Resource resource = getResourceByType(anotherResource.getType());
        resource.minus(anotherResource);
    }

    @Override
    public void provide(Consumer consumer) throws InsufficientResourcesException {
        if (!consumer.isSatisfiedBy(this)) {
            throw new InsufficientResourcesException();
        }
        consumer.providedBy(this);
    }
    
    @Override
    public void consume(Consumer consumer) {
        consumer.consumedBy(this);
    }
    
    @Override
    public void demand(Resource anotherResource) {
        Resource resource = getResourceByType(anotherResource.getType());
        resource.plus(anotherResource);
    }
    
    @Override
    public Resource getResourceByType(Type type) {
        return resources.get(type);
    }
}
