package com.makiah.makiahsandroidlib.threading;

import java.util.ArrayList;

/**
 * Used to separate SimpleTasks into certain groups, depending on their function.  Can either run
 * asynchronously or synchronously depending on the mode you set.  Async by default.
 */
public class ScheduledTaskPackage extends ParallelTask
{
    private enum ScheduledUpdateMode {ASYNCHRONOUS, SYNCHRONOUS}
    private ScheduledUpdateMode currentUpdateMode = ScheduledUpdateMode.ASYNCHRONOUS;

    public final String groupName;
    public ScheduledTaskPackage(TaskParent parent, String groupName)
    {
        this(parent, groupName, null);
    }
    public ScheduledTaskPackage(TaskParent parent, String groupName, ScheduledTask... tasks)
    {
        super(parent, groupName);

        this.groupName = groupName;

        if (tasks == null)
            return;

        //Populate task list.
        for (ScheduledTask task : tasks)
            add(task);
    }

    /**
     * Set async/synchronous states.
     */
    public void setUpdateMode(ScheduledUpdateMode updateMode)
    {
        if (updateMode == currentUpdateMode)
            return;

        switch (updateMode)
        {
            case SYNCHRONOUS:
                if (this.isCurrentlyRunning())
                    this.stop();
                break;
        }

        currentUpdateMode = updateMode;
    }

    /**
     * Manually update the thread (run if this is synchronous).
     */
    public void synchronousUpdate() throws InterruptedException
    {
        if (currentUpdateMode == ScheduledUpdateMode.ASYNCHRONOUS)
            return;

        // Just update this task.
        onDoTask();
    }

    /**
     * Place tasks in here, which will be run by the complex task class nested in this class.
     */
    private ArrayList<ScheduledTask> taskList = new ArrayList<> ();
    private ArrayList<ScheduledTask> pendingAddition = new ArrayList<>(); // Separate list because adding elements mid loop in another thread causes crash.
    public void add(ScheduledTask scheduledTask)
    {
        // If not on, add it straight to the task list.
        if (!isCurrentlyRunning())
            taskList.add (scheduledTask);
        else
            pendingAddition.add(scheduledTask);

        scheduledTask.containingPackage = this;
    }
    public void remove(ScheduledTask scheduledTask)
    {
        taskList.remove (scheduledTask);
    }

    /**
     * The TaskPackageRunner is a ComplexTask which just loops through the list of simple tasks that it
     * needs to run and runs each depending on the pause they ask for.
     *
     * Since ComplexTasks already include a ProcessConsole, don't bother making one in here.
     */
    @Override
    protected void onDoTask () throws InterruptedException
    {
        while (true)
        {
            // Cycle through whole list of tasks and run all that can be run.
            for (int i = 0; i < taskList.size(); i++)
            {
                //Run if possible.
                ScheduledTask task = taskList.get(i);
                if (task.isRunning() && task.nextRunTime < System.currentTimeMillis())
                    task.nextRunTime = task.onContinueTask() + System.currentTimeMillis();

                // Exit program if stop requested, otherwise yield to other threads.
                flow.yield();
            }

            // Add all pending tasks (if they exist).
            for (ScheduledTask pendingTask : pendingAddition)
            {
                taskList.add(pendingTask);
                pendingAddition.remove(pendingTask);
            }

            // Exit program if stop requested, otherwise yield to other threads.
            flow.yield();
        }
    }
}