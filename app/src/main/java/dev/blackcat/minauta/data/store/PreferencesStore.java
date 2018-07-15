package dev.blackcat.minauta.data.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.Session;

public class PreferencesStore
{

    public static final String USERNAME = "dev.blackcat.minauta.Username";
    public static final String PASSWORD = "dev.blackcat.minauta.Password";
    public static final String LOGOUT_PARAMS = "dev.blackcat.minauta.LogoutParams";
    public static final String TIME_PARAMS = "dev.blackcat.minauta.TimeParams";
    public static final String START_TIME = "dev.blackcat.minauta.StartTime";

    Context context;

    public PreferencesStore(Context context)
    {
        this.context = context;
    }

    public Account getAccount()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        String username = prefs.getString(USERNAME, "");
        String password = prefs.getString(PASSWORD, "");
        if (username.equals(""))
            return null;

        Session session = null;
        String logoutParams = prefs.getString(LOGOUT_PARAMS, "");
        String timeParams = prefs.getString(TIME_PARAMS, "");
        long startTime = prefs.getLong(START_TIME, 0);
        if (!logoutParams.equals(""))
        {
            session = new Session();
            session.setLogoutParams(logoutParams);
            session.setTimeParams(timeParams);
            session.setStartTime(startTime);
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setSession(session);
        return account;
    }

    public void setAccount(String username, String password)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    public void setSession(String logoutParams, String timeParams, long startTime)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LOGOUT_PARAMS, logoutParams);
        editor.putString(TIME_PARAMS, timeParams);
        editor.putLong(START_TIME, startTime);
        editor.apply();
    }

}
