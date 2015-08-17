package com.ulb.simulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.Subscriber;

public class SimpleServiceLevelAgreementNegotiation implements ServiceLevelAgreementNegotiation, Subscriber {
    private static final long CPU_1IPS = 1;
    private static final long CPU_2IPS = 2;
    private static final long MEMORY_256MO = 256;
    private ServiceLevelAgreement serviceLevelAgreement;
    private Cloud cloud;
    private List<VirtualMachine> virtualMachines = new ArrayList<VirtualMachine>();
    private Iterator<VirtualMachine> iterator;
    private int nbrAllocatedVirtualMachines;
    
    public SimpleServiceLevelAgreementNegotiation(ServiceLevelAgreement serviceLevelAgreement, Cloud cloud) {
        this.serviceLevelAgreement = serviceLevelAgreement;
        this.cloud = cloud;
        EventService.getInstance().subscribe(VirtualMachineAllocationSuccessEvent.class, new VirtualMachineAllocationFilter(virtualMachines), this);
        EventService.getInstance().subscribe(VirtualMachineAllocationFailureEvent.class, new VirtualMachineAllocationFilter(virtualMachines), this);
    }
    
    @Override
    public void negotiate() {
        Specification specification = new Specification();
        specification.addResource(new Resource(Type.CPU, CPU_1IPS));
        specification.addResource(new Resource(Type.MEMORY, MEMORY_256MO));
        virtualMachines.add(new VirtualMachine(specification));
        specification = new Specification();
        specification.addResource(new Resource(Type.CPU, CPU_2IPS));
        specification.addResource(new Resource(Type.MEMORY, MEMORY_256MO));
        virtualMachines.add(new VirtualMachine(specification));
        iterator = virtualMachines.iterator();
        cloud.allocateVirtualMachine(iterator.next());
    }

    @Override
    public void inform(Event event) {
        if (event instanceof VirtualMachineAllocationSuccessEvent) {
            ++nbrAllocatedVirtualMachines;
            if (iterator.hasNext()) {
                cloud.allocateVirtualMachine(iterator.next());
            } else if (nbrAllocatedVirtualMachines == virtualMachines.size()) {
                EventService.getInstance().publishEvent(new ServiceLevelAgreementNegotiationSuccessEvent(this, serviceLevelAgreement));
            }
        } else if (event instanceof VirtualMachineAllocationFailureEvent) {
            EventService.getInstance().publishEvent(new ServiceLevelAgreementNegotiationFailureEvent(this, serviceLevelAgreement));
        }
    }
}
