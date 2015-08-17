package com.ulb.simulator;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private List<Task> tasks = new ArrayList<Task>();

    public void addTask(Task task) {
        tasks.add(task);
    }
    
    public int getNbrTasks() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }
}