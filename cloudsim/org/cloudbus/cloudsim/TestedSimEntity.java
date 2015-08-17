package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

public class TestedSimEntity extends SimEntity {
	private SimEvent lastEvent;
	
	public TestedSimEntity(String name) {
		super(name);
	}
	
	@Override
	public void startEntity() {
		/* Nothing to do */
	}

	@Override
	public void shutdownEntity() {
		/* Nothing to do */
	}

	@Override
	public void processEvent(SimEvent event) {
		lastEvent = event;
	}
	
	public SimEvent getLastEvent() {
		return lastEvent;
	}
	
	public boolean isPaused() {
		return (getState() == SimEntity.HOLDING);
	}
}
