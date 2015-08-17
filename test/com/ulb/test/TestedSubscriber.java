package com.ulb.test;

import java.util.ArrayList;
import java.util.List;

import com.ulb.utility.Event;
import com.ulb.utility.Subscriber;

public class TestedSubscriber implements Subscriber {
    private List<Event> events = new ArrayList<Event>();
    
    @Override
    public void inform(Event event) {
        events.add(event);
    }
    
    public List<Event> getEvents() {
        return events;
    }
}
