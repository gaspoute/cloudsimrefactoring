package com.ulb.simulator;

import java.util.Iterator;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.Subscriber;
import com.ulb.utility.TargetFilter;

public class FirstFitVirtualMachineAllocation implements VirtualMachineAllocation, Subscriber {
    private VirtualMachine virtualMachine;
    private Cloud cloud;
    private Iterator<Datacenter> datacenters;
    private Iterator<Server> servers;
    private Datacenter datacenter;
    private Server server;
    
    public FirstFitVirtualMachineAllocation(VirtualMachine virtualMachine, Cloud cloud) {
        this.virtualMachine = virtualMachine;
        this.cloud = cloud;
    }
    
    @Override
    public void allocate() {
        EventService.getInstance().subscribe(InterrogationAboutVirtualMachineSuccessEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(InterrogationAboutVirtualMachineFailureEvent.class, new TargetFilter(this), this);
        datacenters = cloud.getDatacenters().iterator();
        datacenter = datacenters.next();
        EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineStartEvent(this, datacenter, virtualMachine));
    }

    @Override
    public void inform(Event event) {
        if (event instanceof InterrogationAboutVirtualMachineSuccessEvent) {
            InterrogationAboutVirtualMachineSuccessEvent signal = (InterrogationAboutVirtualMachineSuccessEvent) event;
            if (signal.isFrom(datacenter)) {
                servers = datacenter.getServers().iterator();
                server = servers.next();
                EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineStartEvent(this, server, signal.getVirtualMachine()));
            } else {
                EventService.getInstance().publishEvent(new VirtualMachineAllocationStartEvent(this, signal.getSource(), signal.getVirtualMachine()));
            }
        } else if (event instanceof InterrogationAboutVirtualMachineFailureEvent) {
            InterrogationAboutVirtualMachineFailureEvent signal = (InterrogationAboutVirtualMachineFailureEvent) event;
            if (signal.isFrom(datacenter)) {
                if (!datacenters.hasNext()) {
                    EventService.getInstance().publishEvent(new VirtualMachineAllocationFailureEvent(this, signal.getVirtualMachine()));
                } else {
                    datacenter = datacenters.next();
                    EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineStartEvent(this, datacenter, signal.getVirtualMachine()));
                }
            } else {
                if (!servers.hasNext() && !datacenters.hasNext()) {
                    EventService.getInstance().publishEvent(new VirtualMachineAllocationFailureEvent(this, signal.getVirtualMachine()));
                } else if (!servers.hasNext() && datacenters.hasNext()) {
                    datacenter = datacenters.next();
                    EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineStartEvent(this, datacenter, signal.getVirtualMachine()));
                } else {
                    server = servers.next();
                    EventService.getInstance().publishEvent(new InterrogationAboutVirtualMachineStartEvent(this, server, signal.getVirtualMachine()));
                }
            }
        }
    }
}
