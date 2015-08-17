package com.ulb.simulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.ObjectId;
import com.ulb.utility.Subscriber;
import com.ulb.utility.TargetFilter;

public class Datacenter implements Subscriber {
    private Map<ObjectId, Server> servers = new HashMap<ObjectId, Server>();
    
    public Datacenter() {
        EventService.getInstance().subscribe(InterrogationAboutVirtualMachineStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(InterrogationAboutTaskStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(RunningEvent.class, new TargetFilter(this), this);
    }
    
    public void addServer(Server server) {
        servers.put(server.getId(), server);
    }
    
    public void migrateVirtualMachine(ObjectId sourceId, ObjectId targetId, ObjectId virtualMachineId) throws InsufficientResourcesException {
        VirtualMachine virtualMachine = servers.get(sourceId).deallocateVirtualMachine(virtualMachineId);
        servers.get(targetId).allocateVirtualMachine(virtualMachine);
    }
    
    public void run(long currentTime) {
        for (Server server : servers.values()) {
            EventService.getInstance().publishEvent(new RunningEvent(this, server, currentTime));
        }        
    }
    
    public boolean isSuitable(VirtualMachine virtualMachine) {
        for (Server server : servers.values()) {
            if (server.isSuitable(virtualMachine)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSuitable(Task task) {
        for (Server server : servers.values()) {
            if (server.isSuitable(task)) {
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
        } else if (event instanceof InterrogationAboutTaskStartEvent) {
            InterrogationAboutTaskStartEvent signal = (InterrogationAboutTaskStartEvent) event;
            if (isSuitable(signal.getTask())) {
                EventService.getInstance().publishEvent(new InterrogationAboutTaskSuccessEvent(this, signal.getSource(), signal.getTask()));
            } else {
                EventService.getInstance().publishEvent(new InterrogationAboutTaskFailureEvent(this, signal.getSource(), signal.getTask()));
            }
        } else if (event instanceof RunningEvent) {
            run(event.getWhen());
        }
    }

    public Collection<Server> getServers() {
        return servers.values();
    }
}