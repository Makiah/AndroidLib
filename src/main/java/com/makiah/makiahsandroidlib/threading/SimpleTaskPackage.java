package com.makiah.makiahsandroidlib.threading;

import java.util.ArrayList;

/**
 * Used to separate SimpleTasks into certain groups, depending on their function.
 */
public class SimpleTaskPackage extends ParallelTask
{
    public final String groupName;
    public SimpleTaskPackage (TaskParent parent, String groupName)
    {
        this(parent, groupName, null);
    }
    public SimpleTaskPackage(TaskParent parent, String groupName, SimpleTask... tasks)
    {
        super(parent, groupName);

        this.groupName = groupName;

        if (tasks == null)
            return;

        //Populate task list.
        for (SimpleTask task : tasks)
        {
            add(task);
        }
    }

    /**
     * Place tasks in here, which will be run by the complex task class nested in this class.
     */
    private ArrayList<SimpleTask> taskList = new ArrayList<> ();
    private ArrayList<SimpleTask> pendingAddition = new ArrayList<>(); // Separate list because adding elements mid loop in another thread causes crash.
    public void add(SimpleTask simpleTask)
    {
        // If not on, add it straight to the task list.
        if (!isCurrentlyRunning())
            taskList.add (simpleTask);
        else
            pendingAddition.add(simpleTask);

        simpleTask.containingPackage = this;
    }
    public void remove(SimpleTask simpleTask)
    {
        taskList.remove (simpleTask);
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
        while (true) {
            //Cycle through whole list of tasks and run all that can be run.
            for (int i = 0; i < taskList.size(); i++) {
                //Run if possible.
                SimpleTask task = taskList.get(i);
                if (task.isRunning() && task.nextRunTime < System.currentTimeMillis())
                    task.nextRunTime = task.onContinueTask() + System.currentTimeMillis();

                //Exit program if stop requested, otherwise yield to other threads.
                flow.yield();
            }

            // Add all pending tasks (if they exist).
            for (SimpleTask pendingTask : pendingAddition) {
                taskList.add(pendingTask);
                pendingAddition.remove(pendingTask);
            }

            //Exit program if stop requested, otherwise yield to other threads.
            flow.yield();
        }
    }
}