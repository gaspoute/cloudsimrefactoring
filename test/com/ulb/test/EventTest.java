package com.ulb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

import com.ulb.utility.Event;

public class EventTest {
    private Object source;
    private Object target;
    private Event event;
    
    @Before
    public void setup() {
        source = new Object();
        target = new Object();
        event = new Event(source, target, 0);
    }
    
    @Test
    public void testEquals() {
        Event anotherEvent = new Event(source, target, 0);
        assertTrue(event.equals(event));
        assertFalse(event.equals(null));
        assertFalse(event.equals(new Object()));
        assertFalse(event.equals(new Event(source, target, 5)));
        assertFalse(event.equals(new Event(source, new Object(), 5)));
        assertFalse(event.equals(new Event(new Object(), target, 5)));
        assertFalse(event.equals(anotherEvent) && anotherEvent.equals(event));
        assertFalse(event.hashCode() == anotherEvent.hashCode());
    }
    
    @Test
    public void testCompareTo() {
        Queue<Event> queue = new PriorityQueue<Event>();
        Event event1 = new Event(source);
        Event event2 = new Event(source, 5);
        Event event3 = new Event(source);
        queue.offer(event);
        queue.offer(event3);
        queue.offer(event2);
        queue.offer(event);
        queue.offer(event1);
        assertEquals(event, queue.poll());
        assertEquals(event, queue.poll());
        assertEquals(event1, queue.poll());
        assertEquals(event3, queue.poll());
        assertEquals(event2, queue.poll());
    }
    
    @Test
    public void testIsFrom() {
        Event anotherEvent = new Event(null);
        assertTrue(event.isFrom(source));
        assertFalse(event.isFrom(new Object()));
        assertTrue(anotherEvent.isFrom(null));
        assertFalse(anotherEvent.isFrom(source));
    }
    
    @Test
    public void testIsTo() {
        Event anotherEvent = new Event(null);
        assertTrue(event.isTo(target));
        assertFalse(event.isTo(new Object()));
        assertTrue(anotherEvent.isTo(null));
        assertFalse(anotherEvent.isTo(target));
    }
}
