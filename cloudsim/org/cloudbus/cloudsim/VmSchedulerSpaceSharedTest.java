package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

public class VmSchedulerSpaceSharedTest {
	private static final double VM_MIPS = 1000;
	
	private VmSchedulerSpaceShared vmScheduler;
	private List<Pe> pes = new ArrayList<Pe>();
	private Vm vm0;
	private Vm vm1;

	@Before
	public void setUp() throws Exception {
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(VM_MIPS)));
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(VM_MIPS)));
		vmScheduler = new VmSchedulerSpaceShared(pes);
		vm0 = new Vm(0, 0, VM_MIPS / 4, 1, 0, 0, 0, "", null);
		vm1 = new Vm(1, 0, VM_MIPS / 2, 1, 0, 0, 0, "", null);
	}
	
	@Test
	public void testInit() {
		assertSame(pes, vmScheduler.getPeList());
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getAvailableMips(), 0);
		assertEquals(VM_MIPS, vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);
	}
	
	@Test
	public void testAllocatePesForVm() { // Le test échoue à cause de Provisioner qui n'est pas mis à jour à la fin de l'allocation
		List<Double> mipsShare0 = new ArrayList<Double>();
		mipsShare0.add(VM_MIPS / 4);
		mipsShare0.add(VM_MIPS / 4);
		mipsShare0.add(VM_MIPS / 4);
		mipsShare0.add(VM_MIPS / 4);
		assertFalse(vmScheduler.allocatePesForVm(vm0,  mipsShare0));
		
		mipsShare0.clear();
		mipsShare0.add(VM_MIPS / 4);
		
		assertTrue(vmScheduler.allocatePesForVm(vm0, mipsShare0));

		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4, vmScheduler.getAvailableMips(), 0);
		assertEquals(VM_MIPS, vmScheduler.getMaxAvailableMips(), 0); // Une seule (sur 2) VM est utilisée, l'autre a encore toute sa capacité
		assertEquals(VM_MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);

		List<Double> mipsShare1 = new ArrayList<Double>();
		mipsShare1.add(VM_MIPS / 2);

		assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));

		// 2000 - 250 - 500 = 1250
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4 - VM_MIPS / 2, vmScheduler.getAvailableMips(), 0);
		assertEquals(VM_MIPS - VM_MIPS / 2, vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(VM_MIPS / 2, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

		vmScheduler.deallocatePesForAllVms();

		assertEquals(PeList.getTotalMips(pes), vmScheduler.getAvailableMips(), 0);
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getMaxAvailableMips(), 0);
		assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
	}

	@Test
	public void testDeallocatePesForVm() {
		List<Double> mipsShare = new ArrayList<Double>(); 
		mipsShare.add(VM_MIPS / 4);
		
		vmScheduler.allocatePesForVm(vm0, mipsShare);
		assertEquals(PeList.getTotalMips(pes) - VM_MIPS / 4, vmScheduler.getAvailableMips(), 0);
		vmScheduler.deallocatePesForVm(vm0);
		assertEquals(PeList.getTotalMips(pes), vmScheduler.getAvailableMips(), 0);

	}
}
