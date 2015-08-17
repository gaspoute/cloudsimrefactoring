package com.ulb.simulator;

import java.util.logging.Logger;

import com.ulb.simulator.resource.Resource;
import com.ulb.simulator.resource.Type;
import com.ulb.utility.ObjectId;

public class Task extends Consumer {
    public static enum Status {
        NONE, IN_PROGRESS, CANCELED, PAUSED, COMPLETED
    }
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ObjectId id = new ObjectId();
    private Status status = Status.NONE;
    private long length;
    private long previousTime;
    private long completionTime;
    
    public Task(Specification specification, long length) {
        super(specification);
        this.length = length;
    }
    
    @Override
    public void providedBy(Provider provider) {
        super.providedBy(provider);
        status = Status.IN_PROGRESS;
    }
    
    public void cancel() {
        if (status != Status.CANCELED) {
            status = Status.CANCELED;
        }
    }
    
    public void pause() {
        if (status == Status.IN_PROGRESS) {
            status = Status.PAUSED;
        }
    }
    
    public void resume() {
        if (status == Status.PAUSED) {
            status = Status.IN_PROGRESS;
        }
    }
    
    public void run(long currentTime) {
        Resource cpu = getResourceByType(Type.CPU);
        long executedLength = cpu.getCapacity() * (currentTime - previousTime);
        length -= (executedLength > length ? length : executedLength);
        completionTime = currentTime + (length / cpu.getCapacity());
        LOGGER.info("completion time = "+completionTime+" ("+length+"/"+cpu.getCapacity()+")");
        LOGGER.info("length = "+length);
        if (length == 0) {
            status = Status.COMPLETED;
        }
        previousTime = currentTime;
    }
    
    public ObjectId getId() {
        return id;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public long getCompletionTime() {
        return completionTime;
    }
}