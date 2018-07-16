package dev.blackcat.minauta.data.net;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.Session;

public interface Connection
{

    public enum State
    {
        OK,
        ERROR,
        UNKNOWN_USERNAME,
        INCORRECT_PASSWORD,
        ALREADY_CONNECTED,
    }

    public class LoginResult
    {
        public Session session;
        public State state;
    }

    public class LogoutResult
    {
        public State state;
    }

    LoginResult login(Account account);
    LogoutResult logout(Session session);
}
