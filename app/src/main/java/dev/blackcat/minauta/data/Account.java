package dev.blackcat.minauta.data;

import java.io.Serializable;

public class Account implements Serializable
{

    protected String username;
    protected String password;
    protected Session session;

    public Account()
    {}

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

}
