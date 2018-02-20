package dude.makiah.androidlib.logging;

import android.text.TextUtils;
import android.widget.TextView;

import dude.makiah.androidlib.threading.ParallelTask;
import dude.makiah.androidlib.threading.TaskParent;

import java.util.ArrayList;

public abstract class LoggingBase extends ParallelTask
{
    // Singleton property
    public static LoggingBase instance;

    // Properties required for logging.
    private static final int MAX_SEQUENTIAL_LINES = 13;
    protected ArrayList<String> sequentialConsoleData; //Lines being added and removed.
    protected ArrayList<ProcessConsole> privateProcessConsoles;

    public LoggingBase(TaskParent parent)
    {
        super(parent, "Logging Task"); // Don't provide a logger here because there's no need.

        // Initialize singleton
        instance = this;

        //Initialize required components.
        sequentialConsoleData = new ArrayList<>();
        privateProcessConsoles = new ArrayList<>();
    }

    /**
     * Log a series of lines to the console.
     * @param newLines the lines to log.
     */
    public void lines(String... newLines)
    {
        //Add new line at beginning of the lines.
        for (String line: newLines)
            sequentialConsoleData.add (0, line);
        //If there is more than 5 lines there, remove one.
        while (sequentialConsoleData.size () > MAX_SEQUENTIAL_LINES)
            sequentialConsoleData.remove (MAX_SEQUENTIAL_LINES);
    }

    /**
     * To get a private process console, create a new Log.ProcessConsole(<name here>) and then run write() to provide new content.
     */
    public ProcessConsole newProcessConsole(String name)
    {
        return new ProcessConsole(name, privateProcessConsoles);
    }

    /**
     * Returns the entire console as a single concatenated string.
     */
    protected String getConsoleAsString()
    {
        String entireConsole = "";
        entireConsole = entireConsole.concat("—————— Sequential Data —————————\n");
        entireConsole = entireConsole.concat(TextUtils.join("\n", sequentialConsoleData));

        if (privateProcessConsoles.size() > 0)
        {
            entireConsole = entireConsole.concat("\n—————— Process Console Data —————————\n");
            for (ProcessConsole processConsole : privateProcessConsoles)
            {
                entireConsole = entireConsole.concat(TextUtils.join("\n", processConsole.processData));
            }
        }

        return entireConsole;
    }

    /**
     * Override: tells the console what to do for on-screen logging.
     */
    protected abstract void refreshOnScreenConsole();

    /**
     * The task which updates the console at a fairly slow rate but your eye can't tell the difference.
     */
    @Override
    protected void onDoTask () throws InterruptedException
    {
        while (true)
        {
            refreshOnScreenConsole();
            flow.msPause(300);
        }
    }
}
