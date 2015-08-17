package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.SimEvent;

public class TestedDatacenter extends Datacenter {
	private List<SimEvent> events = new ArrayList<SimEvent>();
	
	public TestedDatacenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
	}

	@Override
	public void processEvent(SimEvent ev) {
		events.add(ev);
	    super.processEvent(ev);
	}
	
	public List<SimEvent> getEvents() {
		return events;
	}
	
	public List<Integer> getTags() {
	    List<Integer> tags = new ArrayList<Integer>();
	    for (SimEvent event : events) {
	        tags.add(event.getTag());
	    }
	    return tags;
	}
}
