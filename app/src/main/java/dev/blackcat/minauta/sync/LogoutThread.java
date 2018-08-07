package dev.blackcat.minauta.sync;

import dev.blackcat.jnauta.net.Authentication;
import dev.blackcat.jnauta.net.ConnectionBuilder;
import dev.blackcat.minauta.data.Account;

public class LogoutThread extends SyncRunnable<Boolean>
{

    Account account;

    public LogoutThread(Account account)
    {
        this.account = account;
    }

    @Override
    public void run()
    {
        Authentication authentication = new Authentication(ConnectionBuilder.Method.OK_HTTP, account.getUsername(), account.getPassword(), null);
        boolean r = authentication.logout(account.getSession().getLogoutParams());
        this.setResult(r);
    }
}