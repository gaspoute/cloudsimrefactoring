package com.ulb.simulator;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public interface Provider {
    public void addResource(Resource resource);
    public boolean satisfies(Resource resource);
    public void provide(Consumer consumer) throws InsufficientResourcesException;
    public void offer(Resource resource);
    public void consume(Consumer consumer);
    public void demand(Resource resource);
    public Resource getResourceByType(Type type);
}
