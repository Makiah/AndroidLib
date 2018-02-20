package dude.makiah.androidlib.logging;

import android.text.TextUtils;
import android.widget.TextView;

import dude.makiah.androidlib.threading.ParallelTask;

import java.util.ArrayList;

/**
 * The Advanced Console is an easy way to visualize a large number of tasks in parallel without having to rely
 * on superhuman vision.  It also supports sequential logging in the same window.
 *
 * This console uses a task to update its content so that it isn't erratic when displayed on the screen.
 *
 * Local so that resetting is not an issue.
 */
public abstract class OnScreenLog extends LoggingBase
{
    private final OnScreenLogParent parent;
    private final TextView log;

    public OnScreenLog(OnScreenLogParent parent, TextView log)
    {
        super(parent);

        // Required to log to a text view item.
        this.parent = parent;
        this.log = log;

        this.run();
    }

    /**
     * Rebuilds the whole console (call minimally, allow the task to take care of it.)
     */
    public void refreshOnScreenConsole ()
    {
        final String consoleContents = getConsoleAsString();

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.setText(consoleContents);
            }
        });
    }
}
