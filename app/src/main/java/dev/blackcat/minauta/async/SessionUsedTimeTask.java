package dev.blackcat.minauta.async;

import android.content.Context;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import dev.blackcat.minauta.data.Session;
import dev.blackcat.minauta.data.store.PreferencesStore;

public class SessionUsedTimeTask
{

    public interface OnTaskResult
    {
        void onResult(String time);
    }

    Context context;
    OnTaskResult result;

    public SessionUsedTimeTask(Context context, OnTaskResult result)
    {
        this.context = context;
        this.result = result;
    }

    public void execute()
    {
        PreferencesStore store = new PreferencesStore(context);
        final Session session = store.getAccount().getSession();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                long time = Calendar.getInstance().getTimeInMillis();
                long diff = (time - session.getStartTime()) / 1000;

                long minutes = diff / 60;
                long seconds = diff % 60;

                long hours = minutes / 60;
                minutes = minutes % 60;

                String timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                if (result != null)
                    result.onResult(timeStr);
            }
        }, Calendar.getInstance().getTime(), 1000);
    }

}
