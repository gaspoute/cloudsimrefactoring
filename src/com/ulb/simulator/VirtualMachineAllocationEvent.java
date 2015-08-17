package com.ulb.simulator;

import com.ulb.utility.Event;

public class VirtualMachineAllocationEvent extends Event {
    private VirtualMachine virtualMachine;
    
    public VirtualMachineAllocationEvent(Object source, VirtualMachine virtualMachine) {
        super(source);
        this.virtualMachine = virtualMachine;
    }

    public VirtualMachineAllocationEvent(Object source, Object target, VirtualMachine virtualMachine) {
        super(source, target);
        this.virtualMachine = virtualMachine;
    }
    
    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }
}
