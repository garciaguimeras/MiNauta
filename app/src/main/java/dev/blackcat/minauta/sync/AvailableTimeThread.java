package dev.blackcat.minauta.sync;

import dev.blackcat.jnauta.net.Authentication;
import dev.blackcat.jnauta.net.ConnectionBuilder;
import dev.blackcat.minauta.data.Account;

public class AvailableTimeThread extends SyncRunnable<String>
{

    Account account;

    public AvailableTimeThread(Account account)
    {
        this.account = account;
    }

    @Override
    public void run()
    {
        Authentication authentication = new Authentication(ConnectionBuilder.Method.OK_HTTP, account.getUsername(), account.getPassword(), null);
        String r = authentication.getAvailableTime(account.getSession().getTimeParams());
        this.setResult(r);
    }
}