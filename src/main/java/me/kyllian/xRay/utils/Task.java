package me.kyllian.xRay.utils;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class Task extends BukkitRunnable {

    private TaskType taskType;
    private boolean cancelled;

    public Task(TaskType taskType) {
        this.taskType = taskType;
    }

    public abstract void restore();

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
