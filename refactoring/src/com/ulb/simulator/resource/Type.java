package com.ulb.simulator.resource;

public abstract class Type {
    private static final int HASHCODE_PRIME = 31;
    public static final Type CPU = new Cpu();
    public static final Type MEMORY = new Memory();
    public static final Type STORAGE = new Storage();
    private final String name;
    
    protected Type(String name) {
        this.name = name;
    }
    
    @Override
    public int hashCode() {
        return HASHCODE_PRIME + ((name == null) ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof Type)) {
            return false;
        }
        Type type = (Type) object;
        return (name == null && type.name == null) || (name != null && name.equals(type.name));
    }

    public abstract boolean meets(Resource resource, Resource another);
}
