package dev.blackcat.minauta.sync;

import android.util.Log;

public class SyncThread
{

    public static <T> T execute(SyncRunnable<T> runnable)
    {
        if (runnable == null)
            return null;

        try
        {
            Thread thread = new Thread(runnable);
            Log.i(SyncThread.class.toString(), "Calling thread.start");
            thread.start();
            Log.i(SyncThread.class.toString(), "Calling thread.join");
            thread.join();
            Log.i(SyncThread.class.toString(), "Thread returned, getting results");
            return runnable.getResult();
        }
        catch (Exception e)
        {
            Log.i(SyncThread.class.toString(), "Error on thread.join");
            e.printStackTrace();
        }

        return null;
    }

}
