package com.ulb.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ulb.simulator.resource.Resource;
import com.ulb.utility.Event;
import com.ulb.utility.EventService;
import com.ulb.utility.ObjectId;
import com.ulb.utility.Subscriber;
import com.ulb.utility.TargetFilter;

public class VirtualMachine extends Consumer implements Subscriber {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ObjectId id = new ObjectId();
    private Provider provider = new DefaultProvider();
    private Map<ObjectId, Task> tasks = new HashMap<ObjectId, Task>();
    private Scheduler scheduler = new SimpleScheduler();
    
    public VirtualMachine(Specification specification) {
        super(specification);
        EventService.getInstance().subscribe(InterrogationAboutTaskStartEvent.class, new TargetFilter(this), this);
        EventService.getInstance().subscribe(TaskSubmissionStartEvent.class, new TargetFilter(this), this);
    }
    
    public void addResource(Resource resource) {
        super.addResource(resource);
        provider.addResource(resource);
    }
    
    public void submitTask(Task task) throws InsufficientResourcesException {
        provider.provide(task);
        tasks.put(task.getId(), task);
        scheduler.addTask(task);
    }

    public void cancelTask(ObjectId taskId) {
        Task task = tasks.get(taskId);
        task.cancel();
    }

    public void pauseTask(ObjectId taskId) {
        Task task = tasks.get(taskId);
        task.pause();
    }

    public void resumeTask(ObjectId taskId) {
        Task task = tasks.get(taskId);
        task.resume();
    }
    
    public void run(long currentTime) {
        scheduler.schedule(currentTime);
    }
    
    public boolean isSuitable(Consumer consumer) {
        return consumer.isSatisfiedBy(provider);
    }
    
    @Override
    public void inform(Event event) {
        if (event instanceof InterrogationAboutTaskStartEvent) {
            InterrogationAboutTaskStartEvent signal = (InterrogationAboutTaskStartEvent) event;
            if (isSuitable(signal.getTask())) {
                EventService.getInstance().publishEvent(new InterrogationAboutTaskSuccessEvent(this, signal.getSource(), signal.getTask()));
            } else {
                EventService.getInstance().publishEvent(new InterrogationAboutTaskFailureEvent(this, signal.getSource(), signal.getTask()));
            }
        } else if (event instanceof TaskSubmissionStartEvent) {
            TaskSubmissionStartEvent signal = (TaskSubmissionStartEvent) event;
            try {
                submitTask(signal.getTask());
                EventService.getInstance().publishEvent(new TaskSubmissionSuccessEvent(this, signal.getSource(), signal.getTask()));
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "An exception was thrown during the submission of a task", exception);
                EventService.getInstance().publishEvent(new TaskSubmissionFailureEvent(this, signal.getSource(), signal.getTask()));
            }
        }
    }
    
    public ObjectId getId() {
        return id;
    }
    
    public long getCompletionTime() {
        return scheduler.getCompletionTime();
    }
}