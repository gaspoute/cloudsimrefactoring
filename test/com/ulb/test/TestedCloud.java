package com.ulb.test;

import java.util.ArrayList;
import java.util.List;

import com.ulb.simulator.Cloud;
import com.ulb.utility.Event;

public class TestedCloud extends Cloud {
    private List<Event> events = new ArrayList<Event>();
    
    @Override
    public void inform(Event event) {
        events.add(event);
    }
    
    public List<Event> getEvents() {
        return events;
    }
}
