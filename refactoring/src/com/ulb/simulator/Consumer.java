package com.ulb.simulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;

public class Consumer {
    private Specification specification;
    private Map<Type, Resource> resources = new HashMap<Type, Resource>();
    
    public Consumer(Specification specification) {
        this.specification = specification;
    }
    
    public void addResource(Resource resource) {
        resources.put(resource.getType(), resource);
    }
    
    public boolean isSatisfiedBy(Provider provider) {
        return specification.isSatisfiedBy(provider);
    }
    
    public void providedBy(Provider provider) {
        specification.appliedBy(provider, this);
    }
    
    public void consumedBy(Provider provider) {
        Iterator<Entry<Type, Resource>> iterator = resources.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Type, Resource> entry = iterator.next();
            provider.demand(entry.getValue());
            iterator.remove();
        }
    }
    
    public Resource getResourceByType(Type type) {
        return resources.get(type);
    }
    
    public Collection<Resource> getResources() {
        return resources.values();
    }
}