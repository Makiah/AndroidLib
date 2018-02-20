package dude.makiah.androidlib.logging;

import dude.makiah.androidlib.threading.TaskParent;

public interface OnScreenLogParent extends TaskParent
{
    void runOnUiThread(Runnable action);
}
