package com.ulb.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.Subscription;

public class EventServiceTest {
    
    @Test
    public void testSubscribe() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(Event.class, null, subscriber);
        EventService.getInstance().publishEvent(new Event(subscriber));
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertFalse(events.isEmpty());
    }
    
    @Test
    public void testUnsubscribe() {
        TestedSubscriber subscriber = new TestedSubscriber();
        Subscription subscription = EventService.getInstance().subscribe(Event.class, null, subscriber);
        EventService.getInstance().publishEvent(new Event(subscriber));
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        assertFalse(events.isEmpty());
        events.clear();
        EventService.getInstance().unsubscribe(subscription);
        EventService.getInstance().publishEvent(new Event(subscriber));
        EventService.getInstance().dispatch();
        assertTrue(events.isEmpty());
    }
    
    @Test
    public void testPublishEvent() {
        TestedSubscriber subscriber = new TestedSubscriber();
        EventService.getInstance().subscribe(Event.class, null, subscriber);
        Event event1 = new Event(subscriber, 5);
        Event event2 = new Event(subscriber);
        EventService.getInstance().publishEvent(event1);
        EventService.getInstance().publishEvent(event2);
        EventService.getInstance().dispatch();
        List<Event> events = subscriber.getEvents();
        Event[] actual = events.toArray(new Event[events.size()]);
        assertArrayEquals(new Event[] {event2, event1}, actual);
    }
    
    @After
    public void tearDown() {
        EventService.getInstance().unsubscribeAll();
    }
}
