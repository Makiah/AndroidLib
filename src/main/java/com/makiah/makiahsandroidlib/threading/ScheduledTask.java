package com.makiah.makiahsandroidlib.threading;

/**
 * NiFTSimpleTasks run in a single thread context, and are cycled through one by one in order to avoid counting toward the thread limit.
 */
public abstract class ScheduledTask
{
    public final String taskName;
    public ScheduledTaskPackage containingPackage;
    private boolean running = true;
    public long nextRunTime = 0;

    public ScheduledTask()
    {
        this ("Unnamed Simple Task");
    }
    public ScheduledTask(String taskName)
    {
        this.taskName = taskName;
    }

    // Used to start/pause the task.
    public void resume()
    {
        running = true;
    }
    public void pause()
    {
        running = false;
    }
    public boolean isRunning()
    {
        return running;
    }

    // The long returned indicates the amount of time to wait before running the task again.
    protected abstract long onContinueTask () throws InterruptedException;
}