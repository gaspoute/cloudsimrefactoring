package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

public class VmAllocationPolicySimpleTest {
	private static final int HOST_MIPS = 1000;
	private static final int HOST_RAM = 1024;
	private static final int HOST_BW = 1000;
	private static final long HOST_STORAGE = 1000;
	
	private List<Host> hosts = new ArrayList<Host>();
	private VmAllocationPolicySimple allocationPolicy;
	
	@Before
	public void setUp() throws Exception {
		List<Pe> pes = new ArrayList<Pe>();
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		
		Host host0 = new Host(hosts.size(), new RamProvisionerSimple(HOST_RAM), new BwProvisionerSimple(HOST_BW), HOST_STORAGE, pes.subList(0, 2), new VmSchedulerTimeShared(pes.subList(0, 2)));
	    hosts.add(host0);
		Host host1 = new Host(hosts.size(), new RamProvisionerSimple(HOST_RAM), new BwProvisionerSimple(HOST_BW), HOST_STORAGE, pes.subList(2, 4), new VmSchedulerTimeShared(pes.subList(2,  4)));
		hosts.add(host1);
				
		allocationPolicy = new VmAllocationPolicySimple(hosts);
	}
	
	@Test
	public void testAllocateHostForVm() {
		Vm vm = new Vm(0, 0, HOST_MIPS / 2, 2, HOST_RAM / 4, HOST_BW / 5, HOST_STORAGE / 5, "", new CloudletSchedulerTimeShared());
		Host host = hosts.get(0);

		assertEquals(HOST_STORAGE, host.getStorage());
		assertEquals(HOST_RAM, host.getRamProvisioner().getAvailableRam());
		assertEquals(HOST_BW, host.getBwProvisioner().getAvailableBw());
		assertEquals(HOST_MIPS * 2, host.getAvailableMips(), 0);
		assertTrue(allocationPolicy.allocateHostForVm(vm));
		assertSame(host, vm.getHost());
		assertEquals(HOST_STORAGE - (HOST_STORAGE / 5), host.getStorage()); // 1000 - 200 = 800
		assertEquals(HOST_RAM - (HOST_RAM / 4), host.getRamProvisioner().getAvailableRam()); // 1024 - 256 = 768
		assertEquals(HOST_BW - (HOST_BW / 5), host.getBwProvisioner().getAvailableBw()); // 1000 - 200 = 800
		assertEquals((HOST_MIPS * 2) - ((HOST_MIPS / 2) * 2), host.getAvailableMips(), 0); // 2000 - (500 * 2) = 1000
	}
	
	@Test
	public void testDeallocateHostForVm() {
		Vm vm = new Vm(0, 0, HOST_MIPS / 2, 2, HOST_RAM / 4, HOST_BW / 5, HOST_STORAGE / 5, "", new CloudletSchedulerTimeShared());
		Host host = hosts.get(0);

		allocationPolicy.allocateHostForVm(vm);
		
		assertSame(host, vm.getHost());
		assertEquals(HOST_STORAGE - (HOST_STORAGE / 5), host.getStorage());
		assertEquals(HOST_RAM - (HOST_RAM / 4), host.getRamProvisioner().getAvailableRam());
		assertEquals(HOST_BW - (HOST_BW / 5), host.getBwProvisioner().getAvailableBw());
		assertEquals((HOST_MIPS * 2) - ((HOST_MIPS / 2) * 2), host.getAvailableMips(), 0);
		
		allocationPolicy.deallocateHostForVm(vm);
		
		assertNull(vm.getHost());
		assertEquals(HOST_STORAGE, host.getStorage());
		assertEquals(HOST_RAM, host.getRamProvisioner().getAvailableRam());
		assertEquals(HOST_BW, host.getBwProvisioner().getAvailableBw());
		assertEquals(HOST_MIPS * 2, host.getAvailableMips(), 0);
	}

}
