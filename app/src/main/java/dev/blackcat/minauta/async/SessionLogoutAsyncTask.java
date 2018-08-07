package dev.blackcat.minauta.async;

import android.content.Context;
import android.os.AsyncTask;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.store.PreferencesStore;
import dev.blackcat.minauta.net.Connection;
import dev.blackcat.minauta.net.ConnectionFactory;
import dev.blackcat.minauta.net.JNautaConnection;

public class SessionLogoutAsyncTask extends AsyncTask<Void, Void, Connection.LogoutResult>
{

    public interface OnTaskResult
    {
        void onResult(Connection.LogoutResult result);
    }

    Context context;
    OnTaskResult onTaskResult;

    public SessionLogoutAsyncTask(Context context, OnTaskResult onTaskResult)
    {
        this.context = context;
        this.onTaskResult = onTaskResult;
    }

    @Override
    protected Connection.LogoutResult doInBackground(Void... params)
    {
        PreferencesStore store = new PreferencesStore(this.context);
        Account account = store.getAccount();

        Connection connection = ConnectionFactory.createSessionProducer(JNautaConnection.class);
        Connection.LogoutResult result = connection.logout(account);

        return result;
    }

    @Override
    protected void onPostExecute(Connection.LogoutResult result)
    {
        if (onTaskResult != null)
            onTaskResult.onResult(result);
    }
}
