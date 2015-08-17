/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.junit.Before;
import org.junit.Test;

/**
 * @author      Anton Beloglazov
 * @since       CloudSim Toolkit 2.0
 */
public class VmTest {
	private static final int VM_ID = 1;
	private static final int USER_ID = 1;
	private static final double VM_MIPS = 1000;
	private static final int VM_PES_NUMBER = 2;
	private static final int VM_RAM = 1024;
	private static final int VM_BW = 10000;
	private static final long VM_SIZE = 1000;
	private static final String VM_VMM = "Xen";
	
	private CloudletSchedulerDynamicWorkload cloudletScheduler;
	private Vm vm;

	@Before
	public void setUp() throws Exception {
		cloudletScheduler = new CloudletSchedulerDynamicWorkload(VM_MIPS, VM_PES_NUMBER);
		vm = new Vm(VM_ID, USER_ID, VM_MIPS, VM_PES_NUMBER, VM_RAM, VM_BW, VM_SIZE, VM_VMM, cloudletScheduler);
	}

	@Test
	public void testGetMips() {
		assertEquals(VM_MIPS, vm.getMips(), 0);
	}

	@Test
	public void testSetMips() {
		vm.setMips(VM_MIPS / 2);
		assertEquals(VM_MIPS / 2, vm.getMips(), 0);
	}

	@Test
	public void testGetNumberOfPes() {
		assertEquals(VM_PES_NUMBER, vm.getNumberOfPes());
	}

	@Test
	public void testGetRam() {
		assertEquals(VM_RAM, vm.getRam());
	}

	@Test
	public void testGetBw() {
		assertEquals(VM_BW, vm.getBw());
	}

	@Test
	public void testGetSize() {
		assertEquals(VM_SIZE, vm.getSize());
	}

	@Test
	public void testGetVmm() {
		assertEquals(VM_VMM, vm.getVmm());
	}

	@Test
	public void testGetHost() {
		assertEquals(null, vm.getHost());
		Host host = new Host(0, null, null, 0, new ArrayList<Pe>(), null);
		vm.setHost(host);
		assertEquals(host, vm.getHost());
	}

	@Test
	public void testIsInMigration() {
		assertFalse(vm.isInMigration());
		vm.setInMigration(true);
		assertTrue(vm.isInMigration());
	}

	@Test
	public void testGetTotalUtilization() {
		assertEquals(0, vm.getTotalUtilizationOfCpu(0), 0);
	}

	@Test
	public void testGetTotalUtilizationMips() {
		assertEquals(0, vm.getTotalUtilizationOfCpuMips(0), 0);
	}

	@Test
	public void testGetUid() {
		assertEquals(USER_ID + "-" + VM_ID, vm.getUid());
	}

	@Test
	public void testUpdateVmProcessing() {
		assertEquals(0, vm.updateVmProcessing(0, null), 0);
		ArrayList<Double> mipsShare0 = new ArrayList<Double>();
		mipsShare0.add(1.0);
		ArrayList<Double> mipsShare1 = new ArrayList<Double>();
		mipsShare1.add(1.0);
		assertEquals(cloudletScheduler.updateVmProcessing(0, mipsShare0), vm.updateVmProcessing(0, mipsShare1), 0);
	}

	@Test
	public void testGetCurrentAllocatedSize() {
		assertEquals(0, vm.getCurrentAllocatedSize());
		vm.setCurrentAllocatedSize(VM_SIZE);
		assertEquals(VM_SIZE, vm.getCurrentAllocatedSize());
	}

	@Test
	public void testGetCurrentAllocatedRam() {
		assertEquals(0, vm.getCurrentAllocatedRam());
		vm.setCurrentAllocatedRam(VM_RAM);
		assertEquals(VM_RAM, vm.getCurrentAllocatedRam());
	}

	@Test
	public void testGetCurrentAllocatedBw() {
		assertEquals(0, vm.getCurrentAllocatedBw());
		vm.setCurrentAllocatedBw(VM_BW);
		assertEquals(VM_BW, vm.getCurrentAllocatedBw());
	}

	@Test
	public void testGetCurrentAllocatedMips() {
		assertNull(vm.getCurrentAllocatedMips());
	}

	@Test
	public void testIsBeingInstantiated() {
		assertTrue(vm.isBeingInstantiated());
		vm.setBeingInstantiated(false);
		assertFalse(vm.isBeingInstantiated());
	}
}
