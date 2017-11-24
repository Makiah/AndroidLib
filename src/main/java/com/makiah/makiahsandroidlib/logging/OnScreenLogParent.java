package com.makiah.makiahsandroidlib.logging;

import com.makiah.makiahsandroidlib.threading.TaskParent;

public interface OnScreenLogParent extends TaskParent
{
    void runOnUiThread(Runnable action);
}
