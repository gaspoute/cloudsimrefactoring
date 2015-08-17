package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.junit.Ignore;
import org.junit.Test;

public class CloudSimTest {
	private static final int NB_USERS = 1;
	private static final Calendar CALENDAR = Calendar.getInstance();
	private static final boolean TRACE_FLAG = false;

	@Test
	public void testInit() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		assertNotNull(CloudSim.getEntityList());
		assertEquals(2, CloudSim.getNumEntities());
		assertEquals(0, CloudSim.clock(), 0);
		assertFalse(CloudSim.running());
		assertNotNull(CloudSim.getSimulationCalendar());
		assertEquals(1, CloudSim.getCloudInfoServiceEntityId());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInitWithInvalPeriodBetweenEvents() throws Exception {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG, -1);
	}
	
	@Test
	public void testAddEntity() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		assertSame(new TestedSimEntity("Test"), CloudSim.getEntity("Test"));
	}
	
	@Test
	public void testStartSimulation() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		assertEquals(0, CloudSim.startSimulation(), 0);
	}
	
	@Ignore
	public void testTerminateSimulation() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		assertFalse(CloudSim.terminateSimulation(-1));
		assertTrue(CloudSim.terminateSimulation(5));
		SimEntity source = new TestedSimEntity("Source");
		SimEntity destination = new TestedSimEntity("Destination");
		CloudSim.send(source.getId(), destination.getId(), 10, 0, null);
		assertEquals(5, CloudSim.startSimulation(), 0);
	}
	
	@Test
	public void testPauseSimulation() {
		/* Comment tester cette fonction ? */
	}
	
	@Test
	public void testPauseSimulationWithTime() {
	    /* Comment tester cette fonction ? */
	}
	
	@Test
	public void testResumeSimulation() {
		/* Comment tester cette fonction ? */
	}
	
	@Test
	public void testAbruptallyTerminate() {
		/* Comment tester cette fonction ? */
	}
	
	@Test
	public void testSendEvent() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		TestedSimEntity source = new TestedSimEntity("Source");
		TestedSimEntity destination = new TestedSimEntity("Destination");
		CloudSim.send(source.getId(), destination.getId(), 5, 0, null);
		assertEquals(5, CloudSim.startSimulation(), 0);
		assertNotNull(destination.getLastEvent());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSendEventWithNegativeDelay() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		TestedSimEntity source = new TestedSimEntity("Source");
		TestedSimEntity destination = new TestedSimEntity("Destination");
		CloudSim.send(source.getId(), destination.getId(), -1, 0, null);
		assertEquals(5, CloudSim.startSimulation(), 0);
		assertNull(destination.getLastEvent());
	}
	
	@Test
	public void testSendEventFirst() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		TestedSimEntity source = new TestedSimEntity("Source");
		TestedSimEntity destination = new TestedSimEntity("Destination");
		CloudSim.send(source.getId(), destination.getId(), 5, 0, "first");
		CloudSim.sendFirst(source.getId(), destination.getId(), 5, 0, "second");
		assertEquals(5, CloudSim.startSimulation(), 0);
		SimEvent lastEvent = destination.getLastEvent();
		assertNotNull(lastEvent);
		assertEquals("second", lastEvent.getData());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSendEventFirstWithNegativeDelay() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		TestedSimEntity source = new TestedSimEntity("Source");
		TestedSimEntity destination = new TestedSimEntity("Destination");
		CloudSim.send(source.getId(), destination.getId(), 5, 0, "first");
		CloudSim.sendFirst(source.getId(), destination.getId(), -1, 0, "second");
		assertEquals(5, CloudSim.startSimulation(), 0);
		SimEvent lastEvent = destination.getLastEvent();
		assertNotNull(lastEvent);
		assertEquals("first", lastEvent.getData());
	}
	
	@Test
	public void testPauseEntity() {
		CloudSim.init(NB_USERS, CALENDAR, TRACE_FLAG);
		TestedSimEntity entity = new TestedSimEntity("Test");
		assertFalse(entity.isPaused());
		CloudSim.pause(entity.getId(), 5);
		assertTrue(entity.isPaused());
		assertEquals(5, CloudSim.startSimulation(), 0);
		assertFalse(entity.isPaused());
	}
}
