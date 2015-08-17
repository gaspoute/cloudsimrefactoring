package com.ulb.utility;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class EventService {
    private static final EventService INSTANCE = new EventService();
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private PriorityQueue<Event> events = new PriorityQueue<Event>();
    private List<Subscriber> subscribers = new CopyOnWriteArrayList<Subscriber>();
    
    private EventService() {
        
    }
    
    public static EventService getInstance() {
        return INSTANCE;
    }
    
    public Subscription subscribe(Class<? extends Event> type, Filter filter, Subscriber subscriber) {
        Subscription subscription = new Subscription(type, filter, subscriber);
        subscribers.add(subscription);
        return subscription;
    }
    
    public void unsubscribe(Subscription subscription) {
        subscribers.remove(subscription);
    }
    
    public void unsubscribeAll() {
        subscribers.clear();
    }
    
    public void publishEvent(Event event) {
        events.offer(event);
    }
    
    public void dispatch() {
        while (!events.isEmpty()) {
            Event event = events.poll();
            LOGGER.info("Event: "+event);
            for (Subscriber subscriber : subscribers) {
                subscriber.inform(event);
            }
        }
    }
}
