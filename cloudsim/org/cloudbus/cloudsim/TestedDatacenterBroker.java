package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.SimEvent;

public class TestedDatacenterBroker extends DatacenterBroker {
	private List<SimEvent> events = new ArrayList<SimEvent>();
	
	public TestedDatacenterBroker(String name) throws Exception {
		super(name);
	}

	@Override
	public void processEvent(SimEvent ev) {
		events.add(ev);
	    super.processEvent(ev);
	}
	
	List<SimEvent> getEvents() {
		return events;
	}
	
	List<Integer> getTags() {
	    List<Integer> tags = new ArrayList<Integer>();
	    for (SimEvent event : events) {
	        tags.add(event.getTag());
	    }
	    return tags;
	}
}
