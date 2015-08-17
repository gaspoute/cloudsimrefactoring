package com.ulb.simulator;

import java.util.Iterator;

import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.Subscriber;
import com.ulb.utility.TargetFilter;

public class FirstFitJobSubmission implements JobSubmission, Subscriber {
    private Job job;
    private Cloud cloud;
    private int nbrSubmittedTasks;
    private Iterator<Task> tasks;
    private Iterator<Datacenter> datacenters;
    private Iterator<Server> servers;
    private Iterator<VirtualMachine> virtualMachines;
    private Task task;
    private Datacenter datacenter;
    private Server server;
    private VirtualMachine virtualMachine;
    
    public FirstFitJobSubmission(Job job, Cloud cloud) {
        this.job = job;
        this.cloud = cloud;
    }
    
    @Override
    public void submit() {
        EventService.getInstance().subscribe(InterrogationAboutTaskSuccessEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(InterrogationAboutTaskFailureEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(TaskSubmissionSuccessEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(TaskSubmissionFailureEvent.class, new TargetFilter(this), this);
        tasks = job.getTasks().iterator();
        task = tasks.next();
        datacenters = cloud.getDatacenters().iterator();
        interrogateNextDatacenter(task);
    }
    
    @Override
    public void inform(Event event) {
        if (event instanceof InterrogationAboutTaskSuccessEvent) {
            InterrogationAboutTaskSuccessEvent signal = (InterrogationAboutTaskSuccessEvent) event;
            if (signal.isFrom(datacenter)) {
                servers = datacenter.getServers().iterator();
                interrogateNextServer(signal.getTask());
            } else if (signal.isFrom(server)) {
                virtualMachines = server.getVirtualMachines().iterator();
                interrogateNextVirtualMachine(signal.getTask());
            } else {
                EventService.getInstance().publishEvent(new TaskSubmissionStartEvent(this, signal.getSource(), signal.getTask()));
            }
        } else if (event instanceof InterrogationAboutTaskFailureEvent) {
            InterrogationAboutTaskFailureEvent signal = (InterrogationAboutTaskFailureEvent) event;
            if (signal.isFrom(datacenter)) {
                tryNextDatacenter(signal.getTask());
            } else if (signal.isFrom(server)) {
                tryNextServer(signal.getTask());
            } else {
                if (!virtualMachines.hasNext()) {
                    tryNextServer(signal.getTask());
                } else {
                    interrogateNextVirtualMachine(signal.getTask());
                }
            }
        } else if (event instanceof TaskSubmissionSuccessEvent) {
            ++nbrSubmittedTasks;
            if (tasks.hasNext()) {
                task = tasks.next();
                datacenters = cloud.getDatacenters().iterator();
                interrogateNextDatacenter(task);
            } else if (nbrSubmittedTasks == job.getNbrTasks()) {
                EventService.getInstance().publishEvent(new JobSubmissionSuccessEvent(this, job));
            }
        } else if (event instanceof TaskSubmissionFailureEvent) {
            EventService.getInstance().publishEvent(new JobSubmissionFailureEvent(this, job));
        }
    }
    
    private void tryNextDatacenter(Task task) {
        if (!datacenters.hasNext()) {
            EventService.getInstance().publishEvent(new TaskSubmissionFailureEvent(this, this, task));
        } else {
            interrogateNextDatacenter(task);
        }
    }
    
    private void tryNextServer(Task task) {
        if (!servers.hasNext()) {
            tryNextDatacenter(task);
        } else {
            interrogateNextServer(task);
        }
    }
    
    private void interrogateNextDatacenter(Task task) {
        datacenter = datacenters.next();
        EventService.getInstance().publishEvent(new InterrogationAboutTaskStartEvent(this, datacenter, task));
    }
    
    private void interrogateNextServer(Task task) {
        server = servers.next();
        EventService.getInstance().publishEvent(new InterrogationAboutTaskStartEvent(this, server, task));
    }
    
    private void interrogateNextVirtualMachine(Task task) {
        virtualMachine = virtualMachines.next();
        EventService.getInstance().publishEvent(new InterrogationAboutTaskStartEvent(this, virtualMachine, task));
    }
}