package me.kyllian.xRay.tasks;

import me.kyllian.xRay.utils.TaskType;

public abstract class Task extends Thread {

    private TaskType taskType;

    public Task(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
