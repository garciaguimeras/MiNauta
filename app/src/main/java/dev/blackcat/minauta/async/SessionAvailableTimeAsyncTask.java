package dev.blackcat.minauta.async;

import android.content.Context;
import android.os.AsyncTask;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.store.PreferencesStore;
import dev.blackcat.minauta.net.Connection;
import dev.blackcat.minauta.net.ConnectionFactory;
import dev.blackcat.minauta.net.JNautaConnection;

public class SessionAvailableTimeAsyncTask extends AsyncTask<Void, Void, Connection.AvailableTimeResult>
{

    public interface OnTaskResult
    {
        void onResult(Connection.AvailableTimeResult result);
    }

    Context context;
    OnTaskResult onTaskResult;

    public SessionAvailableTimeAsyncTask(Context context, OnTaskResult onTaskResult)
    {
        this.context = context;
        this.onTaskResult = onTaskResult;
    }

    @Override
    protected Connection.AvailableTimeResult doInBackground(Void... params)
    {
        PreferencesStore store = new PreferencesStore(this.context);
        Account account = store.getAccount();

        Connection connection = ConnectionFactory.createSessionProducer(JNautaConnection.class);
        Connection.AvailableTimeResult result = connection.getAvailableTime(account);

        return result;
    }

    @Override
    protected void onPostExecute(Connection.AvailableTimeResult result)
    {
        if (onTaskResult != null)
            onTaskResult.onResult(result);
    }
}
