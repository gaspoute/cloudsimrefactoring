/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

/**
 * @author      Anton Beloglazov
 * @since       CloudSim Toolkit 2.0
 */
public class VmSchedulerTimeSharedTest {
	private static final double VM_MIPS = 1000;

	private VmSchedulerTimeShared vmScheduler;
	private List<Pe> pes = new ArrayList<Pe>();
	private Vm vm0;
	private Vm vm1;

	@Before
	public void setUp() throws Exception {
		pes = new ArrayList<Pe>();
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(VM_MIPS)));
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(VM_MIPS)));
		vmScheduler = new VmSchedulerTimeShared(pes);
		vm0 = new Vm(0, 0, VM_MIPS / 4, 1, 0, 0, 0, "", null);
		vm1 = new Vm(1, 0, VM_MIPS / 2, 2, 0, 0, 0, "", null);
	}

	@Test
	public void testInit() {
		assertSame(pes, vmScheduler.getPeList());
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);
	}

	@Test
	public void testAllocatePesForVm() {
		List<Double> mipsShare0 = new ArrayList<Double>();
		mipsShare0.add(VM_MIPS * 2);

		assertFalse(vmScheduler.allocatePesForVm(vm0, mipsShare0));
		
		mipsShare0.clear();
		mipsShare0.add(VM_MIPS / 4);

		assertTrue(vmScheduler.allocatePesForVm(vm0, mipsShare0));

		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4, vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4, vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(VM_MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);

		List<Double> mipsShare1 = new ArrayList<Double>();
		mipsShare1.add(VM_MIPS / 2);
		mipsShare1.add(VM_MIPS / 8);

		assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));

		// 2000 - 250 - 500 - 125 = 1125
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4 - VM_MIPS / 2 - VM_MIPS / 8, vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4 - VM_MIPS / 2 - VM_MIPS / 8, vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(VM_MIPS / 2 + VM_MIPS / 8, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

		vmScheduler.deallocatePesForAllVms();

		assertEquals(PeList.getTotalMips(pes), vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
	}

	@Test
	public void testAllocatePesForVmInMigration() {
		vm0.setInMigration(true);
		vm1.setInMigration(true);

		List<Double> mipsShare1 = new ArrayList<Double>();
		mipsShare1.add(VM_MIPS / 4);

		assertTrue(vmScheduler.allocatePesForVm(vm0, mipsShare1));

		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4, vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4, vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0.9 * VM_MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);

		List<Double> mipsShare2 = new ArrayList<Double>();
		mipsShare2.add(VM_MIPS / 2);
		mipsShare2.add(VM_MIPS / 8);

		assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare2));

		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4 - VM_MIPS / 2 - VM_MIPS / 8, vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4 - VM_MIPS / 2 - VM_MIPS / 8, vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0.9 * VM_MIPS / 2 + 0.9 * VM_MIPS / 8, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

		vmScheduler.deallocatePesForAllVms();

		assertEquals(PeList.getTotalMips(pes), vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
	}

}
