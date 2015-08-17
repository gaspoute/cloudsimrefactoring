package com.ulb.simulator;

import java.util.List;

import com.ulb.utility.Event;
import com.ulb.utility.Filter;

public class VirtualMachineAllocationFilter implements Filter {
    private List<VirtualMachine> virtualMachines;
    
    public VirtualMachineAllocationFilter(List<VirtualMachine> virtualMachines) {
        this.virtualMachines = virtualMachines;
    }
    
    @Override
    public boolean accepts(Event event) {
        VirtualMachineAllocationEvent signal = (VirtualMachineAllocationEvent) event;
        return virtualMachines.contains(signal.getVirtualMachine());
    }
}
