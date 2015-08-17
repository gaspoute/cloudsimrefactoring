package com.ulb.simulator;

public interface Scheduler {
    public void addTask(Task task);
    public void schedule(long currentTime);
    public long getCompletionTime();
}
