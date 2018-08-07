package dev.blackcat.minauta.sync;

import android.util.Log;

import dev.blackcat.jnauta.net.Authentication;
import dev.blackcat.jnauta.net.AuthenticationResponseParser;
import dev.blackcat.jnauta.net.ConnectionBuilder;
import dev.blackcat.minauta.data.Account;

public class LoginThread extends SyncRunnable<AuthenticationResponseParser.LoginResult>
{

    Account account;

    public LoginThread(Account account)
    {
        this.account = account;
    }

    @Override
    public void run()
    {
        Log.i(LoginThread.class.toString(), "Starting thread");
        Authentication authentication = new Authentication(ConnectionBuilder.Method.OK_HTTP, account.getUsername(), account.getPassword(), null);
        Log.i(LoginThread.class.toString(), "jnauta-net.authentication.login");
        AuthenticationResponseParser.LoginResult r = authentication.login();
        Log.i(LoginThread.class.toString(), "Returned from jnauta-net.authentication.login: " + r);
        this.setResult(r);
        Log.i(LoginThread.class.toString(), "Result set, finishing thread");
    }
}