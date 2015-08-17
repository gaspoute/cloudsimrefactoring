package org.cloudbus.cloudsim;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

public class DatacenterTest {
	private static final String ARCHITECTURE = "x86";
	private static final String OS = "Linux";
	private static final String VMM = "Xen";
		
	private static final int HOST_MIPS = 1000;
	private static final int HOST_RAM = 2048;
	private static final long HOST_STORAGE = 1000000;
	private static final int HOST_BW = 1000;
	
	private static final double TIME_ZONE = 10;
	private static final double COST = 3;
	private static final double COST_PER_RAM = 0.05;
	private static final double COST_PER_STORAGE = 0.001;
	private static final double COST_PER_BW = 0;

	private static final String DATACENTER_NAME = "Datacenter";
	private static final double SCHEDULING_INTERVAL = 0;
		
	private static final String BROKER_NAME = "Broker";
	
	private static final int VM_MIPS = 1000;
	private static final int VM_PES_NUMBER = 1;
	private static final int VM_RAM = 512;
	private static final long VM_BW = 1000;
	private static final long VM_SIZE = 10000;
	
	private static final long CLOUDLET_LENGTH = 5000;
	private static final int CLOUDLET_PES_NUMBER = 1;
	private static final long CLOUDLET_INPUT_SIZE = 300;
	private static final long CLOUDLET_OUTPUT_SIZE = 300;
	
	private TestedDatacenter datacenter;
	private TestedDatacenterBroker broker;
	
	@Before
	public void setUp() throws Exception {
		CloudSim.init(1, Calendar.getInstance(), false);
		
		List<Pe> pes = new ArrayList<Pe>();
		pes.add(new Pe(pes.size(), new PeProvisionerSimple(HOST_MIPS)));
		
		List<Host> hosts = new ArrayList<Host>();
		hosts.add(new Host(hosts.size(), new RamProvisionerSimple(HOST_RAM), new BwProvisionerSimple(HOST_BW), HOST_STORAGE, pes, new VmSchedulerTimeShared(pes)));
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(ARCHITECTURE, OS, VMM, hosts, TIME_ZONE, COST, COST_PER_RAM, COST_PER_STORAGE, COST_PER_BW);
		VmAllocationPolicy allocationPolicy = new VmAllocationPolicySimple(hosts);
		List<Storage> storages = new ArrayList<Storage>();
		datacenter = new TestedDatacenter(DATACENTER_NAME, characteristics, allocationPolicy, storages, SCHEDULING_INTERVAL);
		broker = new TestedDatacenterBroker(BROKER_NAME);
	}
	
	@Test
	public void testInit() {
		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		List<Integer> tags = datacenter.getTags();
		assertArrayEquals(new Integer[] {CloudSimTags.RESOURCE_CHARACTERISTICS}, tags.toArray(new Integer[tags.size()]));
	}
	
	@Test(expected=Exception.class)
	public void testInitWithoutPes() throws Exception {
		List<Pe> pes = new ArrayList<Pe>();
		
		List<Host> hosts = new ArrayList<Host>();
		hosts.add(new Host(hosts.size(), new RamProvisionerSimple(HOST_RAM), new BwProvisionerSimple(HOST_BW), HOST_STORAGE, pes, new VmSchedulerTimeShared(pes)));
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(ARCHITECTURE, OS, VMM, hosts, TIME_ZONE, COST, COST_PER_RAM, COST_PER_STORAGE, COST_PER_BW);
		VmAllocationPolicy allocationPolicy = new VmAllocationPolicySimple(hosts);
		List<Storage> storages = new ArrayList<Storage>();
		new Datacenter("Another", characteristics, allocationPolicy, storages, SCHEDULING_INTERVAL);
	}
	
	@Test
	public void testSubmitVmList() {
		List<Vm> vms = new ArrayList<Vm>();
		vms.add(new Vm(vms.size(), broker.getId(), VM_MIPS, VM_PES_NUMBER, VM_RAM, VM_BW, VM_SIZE, VMM, new CloudletSchedulerTimeShared()));
		broker.submitVmList(vms);
		
		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		
		List<Integer> tags = datacenter.getTags();
		assertArrayEquals(new Integer[] {CloudSimTags.RESOURCE_CHARACTERISTICS, CloudSimTags.VM_CREATE_ACK}, tags.toArray(new Integer[tags.size()]));
	}
	
	@Test
	public void testSubmitCloudletList() {
		List<Vm> vms = new ArrayList<Vm>();
		vms.add(new Vm(vms.size(), broker.getId(), VM_MIPS, VM_PES_NUMBER, VM_RAM, VM_BW, VM_SIZE, VMM, new CloudletSchedulerTimeShared()));
		broker.submitVmList(vms);
		
		List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();
		Cloudlet cloudlet = new Cloudlet(cloudlets.size(), CLOUDLET_LENGTH, CLOUDLET_PES_NUMBER, CLOUDLET_INPUT_SIZE, CLOUDLET_OUTPUT_SIZE, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
		cloudlet.setUserId(broker.getId());
		cloudlets.add(cloudlet);
		broker.submitCloudletList(cloudlets);
		
		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		
		List<Integer> tags = datacenter.getTags();
		assertArrayEquals(new Integer[] {CloudSimTags.RESOURCE_CHARACTERISTICS, CloudSimTags.VM_CREATE_ACK, CloudSimTags.CLOUDLET_SUBMIT, CloudSimTags.VM_DATACENTER_EVENT, CloudSimTags.VM_DESTROY}, tags.toArray(new Integer[tags.size()]));
	}
}
