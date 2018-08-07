package dev.blackcat.minauta.net;

import dev.blackcat.jnauta.net.Authentication;
import dev.blackcat.jnauta.net.AuthenticationResponseParser;
import dev.blackcat.jnauta.net.ConnectionBuilder;
import dev.blackcat.minauta.sync.AvailableTimeThread;
import dev.blackcat.minauta.sync.LoginThread;
import dev.blackcat.minauta.sync.LogoutThread;
import dev.blackcat.minauta.sync.SyncThread;
import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.Session;

public class JNautaConnection implements Connection
{

    @Override
    public LoginResult login(Account account)
    {
        LoginThread thread = new LoginThread(account);
        AuthenticationResponseParser.LoginResult loginResult = thread.execute();

        LoginResult result = new LoginResult();
        if (loginResult == null)
            result.state = State.ERROR;
        else if (loginResult.alreadyConnected)
            result.state = State.ALREADY_CONNECTED;
        else if (loginResult.noMoney)
            result.state = State.NO_MONEY;
        else if (loginResult.badPassword)
            result.state = State.INCORRECT_PASSWORD;
        else if (loginResult.badUsername)
            result.state = State.UNKNOWN_USERNAME;
        else if (loginResult.isGoogle)
            result.state = State.IS_GOOGLE;
        else if (loginResult.session.equals("") || loginResult.timeParams.equals(""))
            result.state = State.ERROR;
        else
        {
            Session session = new Session();
            session.setLogoutParams(loginResult.session);
            session.setTimeParams(loginResult.timeParams);
            result.session = session;
            result.state = State.OK;
        }
        return result;
    }

    @Override
    public LogoutResult logout(Account account)
    {
        LogoutThread thread = new LogoutThread(account);
        Boolean logoutResult = thread.execute();

        LogoutResult result = new LogoutResult();
        result.state = State.OK;
        if (logoutResult == null || !logoutResult)
            result.state = State.ERROR;
        return result;
    }

    @Override
    public AvailableTimeResult getAvailableTime(Account account)
    {
        AvailableTimeThread thread = new AvailableTimeThread(account);
        String time = thread.execute();

        AvailableTimeResult result = new AvailableTimeResult();
        if (time == null)
            result.state = State.ERROR;
        else
        {
            result.state = State.OK;
            result.availableTime = time;
        }
        return result;
    }
}
