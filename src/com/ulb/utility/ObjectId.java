package com.ulb.utility;

public class ObjectId implements Comparable<ObjectId> {
    private static final int HASHCODE_PRIME = 31;
    private static int counter = 0;
    private int value = ++counter;
    
    @Override
    public int hashCode() {
        return HASHCODE_PRIME + value;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || (!(object instanceof ObjectId))) {
            return false;
        }
        ObjectId objectId = (ObjectId) object;
        return value == objectId.value;
    }
    
    @Override
    public int compareTo(ObjectId objectId) {
        if (value < objectId.value) {
            return -1;
        }
        if (value > objectId.value) {
            return 1;
        }
        return 0;
    }
}
