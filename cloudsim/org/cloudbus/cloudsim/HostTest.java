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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

/**
 * @author      Anton Beloglazov
 * @since       CloudSim Toolkit 2.0
 */
public class HostTest {
	private static final int HOST_ID = 0;
	private static final double HOST_MIPS = 1000;
	private static final int HOST_RAM = 1024;
	private static final int HOST_BW = 10000;
	private static final long HOST_STORAGE = Consts.MILLION;
	
	private static final long CLOUDLET_LENGTH = 1000;
	private static final long CLOUDLET_INPUT_SIZE = 300;
	private static final long CLOUDLET_OUTPUT_SIZE = 300;
	private static final int CLOUDLET_PES_NUMBER = 1;
	
	private List<Pe> pes;
	private VmScheduler vmScheduler;
	private Host host;

	@Before
	public void setUp() throws Exception {
		pes = new ArrayList<Pe>();
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		
		vmScheduler = new VmSchedulerTimeShared(pes);
		host = new Host(HOST_ID, new RamProvisionerSimple(HOST_RAM), new BwProvisionerSimple(HOST_BW), HOST_STORAGE, pes, vmScheduler);
	}

	@Test
	public void testIsSuitableForVm() {
		Vm vm0 = new Vm(0, 0, HOST_MIPS, 2, HOST_RAM, HOST_BW, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS, 2));
		Vm vm1 = new Vm(1, 0, HOST_MIPS * 2, 1, HOST_RAM * 2, HOST_BW * 2, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS * 2, 2));

		assertTrue(host.isSuitableForVm(vm0));
		assertFalse(host.isSuitableForVm(vm1));
	}

	@Test
	public void testVmCreate() {
		Vm vm0 = new Vm(0, 0, HOST_MIPS / 2, 1, HOST_RAM / 2, HOST_BW / 2, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS / 2, 1));
		Vm vm1 = new Vm(1, 0, HOST_MIPS, 1, HOST_RAM, HOST_BW, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS, 1));
		Vm vm2 = new Vm(2, 0, HOST_MIPS * 2, 1, HOST_RAM, HOST_BW, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS * 2, 1));
		Vm vm3 = new Vm(3, 0, HOST_MIPS / 2, 2, HOST_RAM / 4, HOST_BW / 4, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS / 2, 2));
		Vm vm4 = new Vm(4, 0, HOST_MIPS / 2, 1, HOST_RAM / 4, HOST_BW / 4, HOST_STORAGE * 2, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS / 2, 1));
		Vm vm5 = new Vm(5, 0, HOST_MIPS * 3, 1, HOST_RAM / 4, HOST_BW / 4, 0, "", new CloudletSchedulerSpaceShared());

		assertTrue(host.vmCreate(vm0));
		assertFalse(host.vmCreate(vm1));
		assertFalse(host.vmCreate(vm2));
		assertTrue(host.vmCreate(vm3));
		assertFalse(host.vmCreate(vm4));
		assertFalse(host.vmCreate(vm5));
	}

	@Test
	public void testVmDestroy() {
		Vm vm = new Vm(0, 0, HOST_MIPS, 1, HOST_RAM / 2, HOST_BW / 2, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS, 1));

		assertTrue(host.vmCreate(vm));
		assertSame(vm, host.getVm(0, 0));
		assertEquals(HOST_MIPS, host.getVmScheduler().getAvailableMips(), 0);

		host.vmDestroy(vm);
		
		assertNull(host.getVm(0, 0));
		assertEquals(0, host.getVmList().size());
		assertEquals(HOST_MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
	}

	@Test
	public void testVmDestroyAll() {
		Vm vm0 = new Vm(0, 0, HOST_MIPS, 1, HOST_RAM / 2, HOST_BW / 2, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS, 1));
		Vm vm1 = new Vm(1, 0, HOST_MIPS, 1, HOST_RAM / 2, HOST_BW / 2, 0, "", new CloudletSchedulerDynamicWorkload(HOST_MIPS, 1));

		assertTrue(host.vmCreate(vm0));
		assertSame(vm0, host.getVm(0, 0));
		assertEquals(HOST_MIPS, host.getVmScheduler().getAvailableMips(), 0);

		assertTrue(host.vmCreate(vm1));
		assertSame(vm1, host.getVm(1, 0));
		assertEquals(0, host.getVmScheduler().getAvailableMips(), 0);

		host.vmDestroyAll();
		
		assertNull(host.getVm(0, 0));
		assertNull(host.getVm(1, 0));
		assertEquals(0, host.getVmList().size());
		assertEquals(HOST_MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
	}

	@Test
	public void testUpdateVmsProcessing() {
		Vm vm0 = new Vm(0, 0, HOST_MIPS, 1, HOST_RAM / 2, HOST_BW / 2, 0, "", new CloudletSchedulerSpaceShared());
		Vm vm1 = new Vm(1, 0, HOST_MIPS, 1, HOST_RAM / 2, HOST_BW / 2, 0, "", new CloudletSchedulerSpaceShared());
		host.vmCreate(vm0);
		host.vmCreate(vm1);
		
		UtilizationModel utilizationModel0 = new UtilizationModelStochastic();
		UtilizationModel utilizationModel1 = new UtilizationModelStochastic();

		Cloudlet cloudlet0 = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel0, utilizationModel0, utilizationModel0);
		Cloudlet cloudlet1 = new Cloudlet(1, CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, utilizationModel1, utilizationModel1, utilizationModel1);
		cloudlet0.setResourceParameter(0, 0, 0);
		cloudlet1.setResourceParameter(0, 0, 0);
		
		host.updateVmsProcessing(0);
		
		assertEquals(vm0.getCloudletScheduler().cloudletSubmit(cloudlet0), 1, 0);
		assertEquals(vm1.getCloudletScheduler().cloudletSubmit(cloudlet1), 1, 0);
		assertEquals(0, host.updateVmsProcessing(1), Double.MAX_VALUE);
	}
}
