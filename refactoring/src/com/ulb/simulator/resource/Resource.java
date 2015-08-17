package com.ulb.simulator.resource;

public class Resource {
    private static final int HASHCODE_PRIME = 31;
    private static final int CONVERSION_32_BITS = 32;
    private Type type;
    private long capacity;

    public Resource(Type type, long capacity) {
        this.type = type;
        this.capacity = capacity;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASHCODE_PRIME * result + ((type == null) ? 0 : type.hashCode());
        result = HASHCODE_PRIME * result + (int) (capacity ^ (capacity >>> CONVERSION_32_BITS));
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof Resource)) {
            return false;
        }
        Resource resource = (Resource) object;
        boolean equalTypes = (type == null && resource.type == null) || (type != null && type.equals(resource.type));
        boolean equalCapacities = capacity == resource.capacity;
        return equalTypes && equalCapacities;
    }

    public boolean meets(Resource resource) {
        if (!resource.hasType(type)) {
            return false;
        }
        return type.meets(this, resource);
    }
    
    public void minus(Resource resource) {
        if (resource.hasType(type)) {
            capacity -= resource.getCapacity();
        }
    }
    
    public void plus(Resource resource) {
        if (resource.hasType(type)) {
            capacity += resource.getCapacity();
        }
    }

    public boolean hasType(Type anotherType) {
        return type.equals(anotherType);
    }

    public Type getType() {
        return type;
    }
    
    public long getCapacity() {
        return capacity;
    }
}