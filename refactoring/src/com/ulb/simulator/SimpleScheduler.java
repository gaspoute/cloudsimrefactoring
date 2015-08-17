package com.ulb.simulator;

import java.util.ArrayList;
import java.util.List;

import com.ulb.simulator.Task.Status;

public class SimpleScheduler implements Scheduler {
    private List<Task> tasks = new ArrayList<Task>();
    private long completionTime;
    
    public void addTask(Task task) {
        tasks.add(task);
    }
    
    @Override
    public void schedule(long currentTime) {
        completionTime = Long.MAX_VALUE;
        for (Task task : tasks) {
            if (task.getStatus() == Status.IN_PROGRESS) {
                task.run(currentTime);
                completionTime = Math.min(completionTime, task.getCompletionTime());
            }
        }
    }
    
    @Override
    public long getCompletionTime() {
        return completionTime;
    }
}