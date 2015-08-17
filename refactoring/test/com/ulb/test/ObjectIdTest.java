package com.ulb.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ulb.utility.ObjectId;

public class ObjectIdTest {
    private ObjectId objectId;
    
    @Before
    public void setup() {
        objectId = new ObjectId();
    }
    
    @Test
    public void testEquals() {
        ObjectId anotherObjectId = new ObjectId();
        assertTrue(objectId.equals(objectId));
        assertFalse(objectId.equals(null));
        assertFalse(objectId.equals(new Object()));
        assertFalse(objectId.equals(anotherObjectId) && anotherObjectId.equals(objectId));
        assertFalse(anotherObjectId.hashCode() == objectId.hashCode());
    }
}
