package dev.blackcat.minauta.net;

import java.util.Calendar;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.Session;

public class FakeConnection implements Connection
{
    @Override
    public LoginResult login(Account account)
    {
        Session session = new Session();
        session.setLogoutParams("?x=x");
        session.setTimeParams("?x=x");
        session.setStartTime(Calendar.getInstance().getTimeInMillis());

        LoginResult result = new LoginResult();
        result.session = session;
        result.state = State.OK;

        return result;
    }

    @Override
    public LogoutResult logout(Session session)
    {
        LogoutResult result = new LogoutResult();
        result.state = State.OK;

        return result;
    }
}
