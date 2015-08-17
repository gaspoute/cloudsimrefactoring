package com.ulb.utility;

public class Subscription implements Subscriber {
    private Class<? extends Event> type;
    private Filter filter;
    private Subscriber subscriber;

    public Subscription(Class<? extends Event> type, Filter filter, Subscriber subscriber) {
        this.type = type;
        this.filter = filter;
        this.subscriber = subscriber;
    }
    
    public void inform(Event event) {
        if (type.isInstance(event) && (filter == null || filter.accepts(event))) {
            subscriber.inform(event);
        }
    }
}
