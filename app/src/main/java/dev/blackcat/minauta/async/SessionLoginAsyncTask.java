package dev.blackcat.minauta.async;

import android.content.Context;
import android.os.AsyncTask;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.store.PreferencesStore;
import dev.blackcat.minauta.net.Connection;
import dev.blackcat.minauta.net.ConnectionFactory;
import dev.blackcat.minauta.net.JNautaConnection;

public class SessionLoginAsyncTask extends AsyncTask<Void, Void, Connection.LoginResult>
{

    public interface OnTaskResult
    {
        void onResult(Connection.LoginResult result);
    }

    Context context;
    OnTaskResult onTaskResult;

    public SessionLoginAsyncTask(Context context, OnTaskResult onTaskResult)
    {
        this.context = context;
        this.onTaskResult = onTaskResult;
    }

    @Override
    protected Connection.LoginResult doInBackground(Void... params)
    {
        PreferencesStore store = new PreferencesStore(this.context);
        Account account = store.getAccount();

        Connection connection = ConnectionFactory.createSessionProducer(JNautaConnection.class);
        Connection.LoginResult result = connection.login(account);

        return result;
    }

    @Override
    protected void onPostExecute(Connection.LoginResult result)
    {
        if (onTaskResult != null)
            onTaskResult.onResult(result);
    }
}
