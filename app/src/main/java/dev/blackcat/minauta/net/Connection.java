package dev.blackcat.minauta.net;

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
        NO_MONEY,
        IS_GOOGLE
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

    public class AvailableTimeResult
    {
        public State state;
        public String availableTime;
    }

    LoginResult login(Account account);
    LogoutResult logout(Account account);
    AvailableTimeResult getAvailableTime(Account account);

}
