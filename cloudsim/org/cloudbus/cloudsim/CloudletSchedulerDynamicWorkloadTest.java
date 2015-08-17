/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.junit.Before;
import org.junit.Test;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class CloudletSchedulerDynamicWorkloadTest {
	private static final long CLOUDLET_LENGTH = 1000;
	private static final long CLOUDLET_INPUT_SIZE = 300;
	private static final long CLOUDLET_OUTPUT_SIZE = 300;
	private static final double MIPS = 1000;
	private static final int PES_NUMBER = 2;
	private CloudletSchedulerDynamicWorkload scheduler;

	@Before
	public void setUp() throws Exception {
		scheduler = new CloudletSchedulerDynamicWorkload(MIPS, PES_NUMBER);
	}

	@Test
	public void testGetNumberOfPes() {
		assertEquals(PES_NUMBER, scheduler.getNumberOfPes());
	}

	@Test
	public void testGetMips() {
		assertEquals(MIPS, scheduler.getMips(), 0);
	}

	@Test
	public void testGetUnderAllocatedMips() {
		UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		ResCloudlet rcl = new ResCloudlet(cloudlet);

		Map<String, Double> underAllocatedMips = new HashMap<String, Double>();
		assertEquals(underAllocatedMips, scheduler.getUnderAllocatedMips());

		underAllocatedMips.put(rcl.getUid(), MIPS / 2);
		scheduler.updateUnderAllocatedMipsForCloudlet(rcl, MIPS / 2);
		assertEquals(underAllocatedMips, scheduler.getUnderAllocatedMips());

		underAllocatedMips.put(rcl.getUid(), MIPS);
		scheduler.updateUnderAllocatedMipsForCloudlet(rcl, MIPS / 2);
		assertEquals(underAllocatedMips, scheduler.getUnderAllocatedMips());
	}

	@Test
	public void testGetCurrentRequestedMips() {
		UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);

		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS);
		mipsShare.add(MIPS);
		scheduler.setCurrentMipsShare(mipsShare);

		assertEquals(mipsShare.size(), scheduler.getCurrentMipsShare().size(), 0);
		assertEquals(mipsShare.get(0), scheduler.getCurrentMipsShare().get(0), 0);
		assertEquals(mipsShare.get(1), scheduler.getCurrentMipsShare().get(1), 0);

		double utilization = utilizationModel.getUtilization(0);

		scheduler.cloudletSubmit(cloudlet);

		List<Double> requestedMips = new ArrayList<Double>();
		requestedMips.add(MIPS * utilization);
		requestedMips.add(MIPS * utilization);

		assertEquals(requestedMips, scheduler.getCurrentRequestedMips());
	}

	@Test
	public void testGetTotalUtilization() {
		UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);

		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS);
		mipsShare.add(MIPS);
		scheduler.setCurrentMipsShare(mipsShare);

		assertEquals(mipsShare.size(), scheduler.getCurrentMipsShare().size());
		assertEquals(mipsShare.get(0), scheduler.getCurrentMipsShare().get(0), 0);
		assertEquals(mipsShare.get(1), scheduler.getCurrentMipsShare().get(1), 0);

		double utilization = utilizationModel.getUtilization(0);

		scheduler.cloudletSubmit(cloudlet, 0);

		assertEquals(utilization, scheduler.getTotalUtilizationOfCpu(0), 0);
	}

	@Test
	public void testCloudletFinish() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);

		scheduler.cloudletSubmit(cloudlet, 0);
		scheduler.cloudletFinish(new ResCloudlet(cloudlet));

		assertEquals(Cloudlet.SUCCESS, scheduler.getCloudletStatus(0));
		assertTrue(scheduler.isFinishedCloudlets());
		assertSame(cloudlet, scheduler.getNextFinishedCloudlet());
	}

	@Test
	public void testGetTotalCurrentMips() {
		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		scheduler.setCurrentMipsShare(mipsShare);

		assertEquals(MIPS / 2, scheduler.getTotalCurrentMips(), 0);
	}

	@Test
	public void testGetTotalCurrentMipsForCloudlet() {
		UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE,
				utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		ResCloudlet rgl = new ResCloudlet(cloudlet);

		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);

		assertEquals(MIPS / 4.0 * PES_NUMBER, scheduler.getTotalCurrentAvailableMipsForCloudlet(rgl, mipsShare), 0);
	}

//	@Test
//	public void testGetEstimatedFinishTimeLowUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//		expect(utilizationModel.getUtilization(0))
//		.andReturn(0.11)
//		.anyTimes();
//		replay(utilizationModel);
//		testGetEstimatedFinishTime(utilizationModel);
//		verify(utilizationModel);
//	}

//	@Test
//	public void testGetEstimatedFinishTimeHighUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//		expect(utilizationModel.getUtilization(0))
//			.andReturn(0.91)
//			.anyTimes();
//		replay(utilizationModel);
//		testGetEstimatedFinishTime(utilizationModel);
//		verify(utilizationModel);
//	}

	@Test
	public void testGetEstimatedFinishTime() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic(); 
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		ResCloudlet rgl = new ResCloudlet(cloudlet);

		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);

		scheduler.setCurrentMipsShare(mipsShare);

		double utilization = utilizationModel.getUtilization(0);
		double availableMipsForCloudlet = (MIPS / 4) * PES_NUMBER;
		double requestedMipsForCloudlet = utilization * (PES_NUMBER * MIPS);
		if (requestedMipsForCloudlet > availableMipsForCloudlet) {
			requestedMipsForCloudlet = availableMipsForCloudlet;
		}

		double expectedFinishTime = ((double) CLOUDLET_LENGTH * PES_NUMBER) / requestedMipsForCloudlet;
		double actualFinishTime = scheduler.getEstimatedFinishTime(rgl, 0);

		assertEquals(expectedFinishTime, actualFinishTime, 0);
	}

//	@Test
//	public void testCloudletSubmitLowUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//		expect(utilizationModel.getUtilization(0))
//			.andReturn(0.11)
//			.anyTimes();
//		replay(utilizationModel);
//		testCloudletSubmit(utilizationModel);
//		verify(utilizationModel);
//	}

//	@Test
//	public void testCloudletSubmitHighUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//		expect(utilizationModel.getUtilization(0))
//			.andReturn(0.91)
//			.anyTimes();
//		replay(utilizationModel);
//		testCloudletSubmit(utilizationModel);
//		verify(utilizationModel);
//	}

	@Test
	public void testCloudletSubmit() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);

		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);

		scheduler.setCurrentMipsShare(mipsShare);

		double utilization = utilizationModel.getUtilization(0);
		double availableMipsForCloudlet = (MIPS / 4) * PES_NUMBER;
		double requestedMipsForCloudlet = utilization * (PES_NUMBER * MIPS);
		if (requestedMipsForCloudlet > availableMipsForCloudlet) {
			requestedMipsForCloudlet = availableMipsForCloudlet;
		}

		double expectedFinishTime = ((double) CLOUDLET_LENGTH * PES_NUMBER) / requestedMipsForCloudlet;
		double actualFinishTime = scheduler.cloudletSubmit(cloudlet);
		assertEquals(cloudlet.getStatus(), Cloudlet.INEXEC);
		assertNotNull(scheduler.getCloudletExecList());
		assertEquals(scheduler.getCloudletExecList().size(), 1);
		assertSame(scheduler.getCloudletExecList().get(0).getCloudlet(), cloudlet);
		assertEquals(expectedFinishTime, actualFinishTime, 0);
	}

//	@Test
//	public void testUpdateVmProcessingLowUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//
//		expect(utilizationModel.getUtilization(0))
//			.andReturn(0.11)
//			.anyTimes();
//
//		expect(utilizationModel.getUtilization(1.0))
//			.andReturn(0.11)
//			.anyTimes();
//
//		replay(utilizationModel);
//
//		testUpdateVmProcessing(utilizationModel);
//
//		verify(utilizationModel);
//	}

//	@Test
//	public void testUpdateVmProcessingHighUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//
//		expect(utilizationModel.getUtilization(0))
//			.andReturn(0.91)
//			.anyTimes();
//
//		expect(utilizationModel.getUtilization(1.0))
//			.andReturn(0.91)
//			.anyTimes();
//
//		replay(utilizationModel);
//
//		testUpdateVmProcessing(utilizationModel);
//
//		verify(utilizationModel);
//	}

//	@Test
//	public void testUpdateVmProcessingLowAndHighUtilization() {
//		UtilizationModel utilizationModel = createMock(UtilizationModel.class);
//
//		expect(utilizationModel.getUtilization(0))
//		.andReturn(0.11)
//		.anyTimes();
//
//		expect(utilizationModel.getUtilization(1.0))
//		.andReturn(0.91)
//		.anyTimes();
//
//		replay(utilizationModel);
//
//		testUpdateVmProcessing(utilizationModel);
//
//		verify(utilizationModel);
//	}

	@Test
	public void testUpdateVmProcessing() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);

		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);
		mipsShare.add(MIPS / 4);

		scheduler.cloudletSubmit(cloudlet);

		double availableMipsForCloudlet = (MIPS / 4) * PES_NUMBER;

		double utilization0 = utilizationModel.getUtilization(0);
		double requestedMipsForCloudlet0 = utilization0 * (PES_NUMBER * MIPS);
		if (requestedMipsForCloudlet0 > availableMipsForCloudlet) {
			requestedMipsForCloudlet0 = availableMipsForCloudlet;
		}

		double expectedCompletiontime0 = ((double) CLOUDLET_LENGTH * PES_NUMBER) / requestedMipsForCloudlet0;
		double actualCompletionTime0 = scheduler.updateVmProcessing(0, mipsShare);
		
		assertEquals(expectedCompletiontime0, actualCompletionTime0, 0);

		double utilization1 = utilizationModel.getUtilization(1);
		double requestedMipsForCloudlet1 = utilization1 * (PES_NUMBER * MIPS);
		if (requestedMipsForCloudlet1 > availableMipsForCloudlet) {
			requestedMipsForCloudlet1 = availableMipsForCloudlet;
		}

		// timeSpan * requestedMipsForCloudlet = le nombre d'instructions exécutées durant un coup d'horloge
		// CLOUDLET_LENGTH * PES_NUMBER - (timeSpan * requestedMipsForCloudlet) = le nombre d'instructions qui restent à exécuter
		double time = 1;
		double timeSpan = 1;
		double expectedCompletiontime1 = time + ((double) CLOUDLET_LENGTH * PES_NUMBER - (timeSpan * requestedMipsForCloudlet0)) / requestedMipsForCloudlet1;
		double actualCompletionTime1 = scheduler.updateVmProcessing(1, mipsShare);
		
		assertEquals(expectedCompletiontime1, actualCompletionTime1, 0);

		assertFalse(scheduler.isFinishedCloudlets());
		assertEquals(0, scheduler.updateVmProcessing(CLOUDLET_LENGTH, mipsShare), 0);
		assertTrue(scheduler.isFinishedCloudlets());
	}

}
