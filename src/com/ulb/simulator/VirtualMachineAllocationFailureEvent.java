package com.ulb.simulator;

public class VirtualMachineAllocationFailureEvent extends VirtualMachineAllocationEvent {
    
    public VirtualMachineAllocationFailureEvent(Object source, VirtualMachine virtualMachine) {
        super(source, virtualMachine);
    }
}
