package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CloudletSchedulerTimeSharedTest {
	private static final long CLOUDLET_LENGTH = 1000;
	private static final long CLOUDLET_INPUT_SIZE = 300;
	private static final long CLOUDLET_OUTPUT_SIZE = 300;
	private static final int CLOUDLET_PES_NUMBER = 2;
	private static final double VM_MIPS = 1000;
	
	private CloudletSchedulerTimeShared scheduler;
	
	@Before
	public void setUp() throws Exception {
		scheduler = new CloudletSchedulerTimeShared();
	}
	
	@Test
	public void testCloudletSubmit() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		
		scheduler.updateVmProcessing(0, Arrays.asList(new Double[] {VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4}));
		
		assertEquals(Cloudlet.CREATED, cloudlet.getCloudletStatus());
		assertEquals(0, scheduler.runningCloudlets());
		assertEquals(4.0, scheduler.cloudletSubmit(cloudlet), 0);
		assertEquals(Cloudlet.INEXEC, cloudlet.getCloudletStatus());
		assertEquals(1, scheduler.runningCloudlets());
	}
	
	@Ignore
	public void testCloudletFinish() {
		// Comment tester cloudletFinish() ? Le cloudlet est supprimé de la liste d'exécution dans updateVmProcessing et pas dans cette méthode.
	}
	
	@Test
	public void testCloudletCancelFromFinishingQueue() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		
		List<Double> mipsShare = Arrays.asList(new Double[] {VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4});
		
		scheduler.updateVmProcessing(0, mipsShare);
		scheduler.cloudletSubmit(cloudlet);
		scheduler.updateVmProcessing(4, mipsShare);
		
		assertEquals(Cloudlet.SUCCESS, cloudlet.getCloudletStatus());
		assertTrue(scheduler.isFinishedCloudlets());
		assertEquals(0, scheduler.runningCloudlets());
		assertSame(cloudlet, scheduler.cloudletCancel(cloudlet.getCloudletId()));
		assertEquals(Cloudlet.SUCCESS, cloudlet.getCloudletStatus());
		assertTrue(scheduler.isFinishedCloudlets());
		assertEquals(0, scheduler.runningCloudlets());
	}
	
	@Test
	public void testCloudletCancelFromExecutingQueue() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
				
		scheduler.updateVmProcessing(0, Arrays.asList(new Double[] {VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4}));
		scheduler.cloudletSubmit(cloudlet);
		
		// Il reste 2000 instructions à exécuter
		assertEquals(Cloudlet.INEXEC, cloudlet.getCloudletStatus());
		assertFalse(scheduler.isFinishedCloudlets());
		assertEquals(1, scheduler.runningCloudlets());
		assertSame(cloudlet, scheduler.cloudletCancel(cloudlet.getCloudletId()));
		assertEquals(Cloudlet.CANCELED, cloudlet.getCloudletStatus());
		assertFalse(scheduler.isFinishedCloudlets());
		assertEquals(0, scheduler.runningCloudlets());
	}
	
	@Test
	public void testCloudletCancelFromPausingQueue() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		
		scheduler.updateVmProcessing(0, Arrays.asList(new Double[] {VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4}));
		scheduler.cloudletSubmit(cloudlet);
		scheduler.cloudletPause(cloudlet.getCloudletId());
		
		assertEquals(Cloudlet.PAUSED, cloudlet.getCloudletStatus());
		assertSame(cloudlet, scheduler.cloudletCancel(cloudlet.getCloudletId()));
		assertEquals(Cloudlet.CANCELED, cloudlet.getCloudletStatus());
	}
	
	@Test
	public void testCloudletPause() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		
		scheduler.updateVmProcessing(0, Arrays.asList(new Double[] {VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4}));
		scheduler.cloudletSubmit(cloudlet);
		
		assertEquals(Cloudlet.INEXEC, cloudlet.getCloudletStatus());
		assertEquals(1, scheduler.runningCloudlets());
		assertTrue(scheduler.cloudletPause(cloudlet.getCloudletId()));
		assertEquals(Cloudlet.PAUSED, cloudlet.getCloudletStatus());
		assertEquals(0, scheduler.runningCloudlets());
	}
	
	@Test
	public void testCloudletResume() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		
		scheduler.updateVmProcessing(0, Arrays.asList(new Double[] {VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4, VM_MIPS / 4}));
		scheduler.cloudletSubmit(cloudlet);		
		scheduler.cloudletPause(cloudlet.getCloudletId());
		
		assertEquals(Cloudlet.PAUSED, cloudlet.getCloudletStatus());
		assertEquals(0, scheduler.runningCloudlets());
		assertEquals(4, scheduler.cloudletResume(cloudlet.getCloudletId()), 0);
		assertEquals(Cloudlet.INEXEC, cloudlet.getCloudletStatus());
		assertEquals(1, scheduler.runningCloudlets());
	}
	
	@Test
	public void testUpdateVmProcessing() {
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		Cloudlet cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel, utilizationModel, utilizationModel);
		cloudlet.setResourceParameter(0, 0, 0);
		
		List<Double> mipsShare = new ArrayList<Double>();
		mipsShare.add(VM_MIPS / 4);
		mipsShare.add(VM_MIPS / 4);
		mipsShare.add(VM_MIPS / 4);
		mipsShare.add(VM_MIPS / 4);
		
		assertEquals(0, scheduler.updateVmProcessing(0, mipsShare), 0);
		
		scheduler.cloudletSubmit(cloudlet);
		
		// capacité moyenne pour chaque CPU (currentCpus est le nombre de CPU càd mipsShare.size())
		// 1000 / 4 = 250
		double capacity = VM_MIPS / 4;
		
		// (1000 * 2) / (250 * 2) = 2000 / 500 = 4
		assertEquals((CLOUDLET_LENGTH * CLOUDLET_PES_NUMBER) / (capacity * CLOUDLET_PES_NUMBER), scheduler.updateVmProcessing(1, mipsShare), 0);
		assertFalse(scheduler.isFinishedCloudlets());
		assertEquals(1, scheduler.runningCloudlets());
		scheduler.updateVmProcessing(4, mipsShare);
		assertTrue(scheduler.isFinishedCloudlets());
		assertEquals(0, scheduler.runningCloudlets());
		assertEquals(0, scheduler.updateVmProcessing(5, mipsShare), 0);
	}

}
