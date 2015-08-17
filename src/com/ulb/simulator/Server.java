package com.ulb.simulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.ObjectId;
import com.ulb.utility.Subscriber;
import com.ulb.utility.TargetFilter;

public class Server extends DefaultProvider implements Subscriber {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ObjectId id = new ObjectId();
    private Map<ObjectId, VirtualMachine> virtualMachines = new HashMap<ObjectId, VirtualMachine>();
    private long completionTime;

    public Server() {
        EventService.getInstance().subscribe(InterrogationAboutVirtualMachineStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(VirtualMachineAllocationStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(InterrogationAboutTaskStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(RunningEvent.class, new TargetFilter(this), this);
    }
       
    public void allocateVirtualMachine(VirtualMachine virtualMachine) throws InsufficientResourcesException {
        provide(virtualMachine);
        virtualMachines.put(virtualMachine.getId(), virtualMachine);
    }

    public VirtualMachine deallocateVirtualMachine(ObjectId virtualMachineId) {
        VirtualMachine virtualMachine = virtualMachines.remove(virtualMachineId);
        consume(virtualMachine);
        return virtualMachine;
    }
    
    public void run(long currentTime) {
        completionTime = Long.MAX_VALUE;
        for (VirtualMachine virtualMachine : virtualMachines.values()) {
            virtualMachine.run(currentTime);
            completionTime = Math.min(completionTime, virtualMachine.getCompletionTime());
        }
    }
    
    public boolean isSuitable(VirtualMachine virtualMachine) {
        return virtualMachine.isSatisfiedBy(this);
    }
    
    public boolean isSuitable(Task task) {
        for (VirtualMachine virtualMachine : virtualMachines.values()) {
            if (virtualMachine.isSuitable(task)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void inform(Event event) {
        if (event instanceof InterrogationAboutVirtualMachineStartEvent) {
            InterrogationAboutVirtualMachineStartEvent signal = (InterrogationAboutVirtualMachineStartEvent) event;
            if (isSuitable(signal.getVirtualMachine())) {
                EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineSuccessEvent(this, signal.getSource(), signal.getVirtualMachine()));
            } else {
                EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineFailureEvent(this, signal.getSource(), signal.getVirtualMachine()));
            }
        } else if (event instanceof VirtualMachineAllocationStartEvent) {
            VirtualMachineAllocationStartEvent signal = (VirtualMachineAllocationStartEvent) event;
            try {
                allocateVirtualMachine(signal.getVirtualMachine());
                EventService.getInstance().publishEvent(new VirtualMachineAllocationSuccessEvent(this, signal.getVirtualMachine()));
            } catch (InsufficientResourcesException exception) {
                LOGGER.log(Level.SEVERE, "An exception was thrown during the allocation of a virtual machine", exception);
                EventService.getInstance().publishEvent(new VirtualMachineAllocationFailureEvent(this, signal.getVirtualMachine()));
            }
        } else if (event instanceof InterrogationAboutTaskStartEvent) {
            InterrogationAboutTaskStartEvent signal = (InterrogationAboutTaskStartEvent) event;
            if (isSuitable(signal.getTask())) {
                EventService.getInstance().publishEvent(new InterrogationAboutTaskSuccessEvent(this, signal.getSource(), signal.getTask()));
            } else {
                EventService.getInstance().publishEvent(new InterrogationAboutTaskFailureEvent(this, signal.getSource(), signal.getTask()));
            }
        } else if (event instanceof RunningEvent) {
            run(event.getWhen());
            if (!isCompleted()) {
                EventService.getInstance().publishEvent(new RunningEvent(this, this, getCompletionTime()));
            }
        }
    }
    
    public boolean isCompleted() {
        return completionTime == Long.MAX_VALUE;
    }
    
    public ObjectId getId() {
        return id;
    }

    public Collection<VirtualMachine> getVirtualMachines() {
        return virtualMachines.values();
    }
    
    public long getCompletionTime() {
        return completionTime;
    }
}