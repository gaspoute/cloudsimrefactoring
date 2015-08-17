package com.ulb.simulator;

import com.ulb.utility.Event;

public class InterrogationAboutVirtualMachineEvent extends Event {
    private VirtualMachine virtualMachine;
    
    public InterrogationAboutVirtualMachineEvent(Object source, Object target, VirtualMachine virtualMachine) {
        super(source, target);
        this.virtualMachine = virtualMachine;
    }
    
    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }
}
