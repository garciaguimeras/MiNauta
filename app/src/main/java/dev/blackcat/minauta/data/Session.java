package dev.blackcat.minauta.data;

import java.io.Serializable;

public class Session implements Serializable
{

    protected String logoutParams;
    protected String timeParams;
    protected long startTime;

    public Session()
    {}

    public String getLogoutParams()
    {
        return logoutParams;
    }

    public void setLogoutParams(String logoutParams)
    {
        this.logoutParams = logoutParams;
    }

    public String getTimeParams()
    {
        return timeParams;
    }

    public void setTimeParams(String timeParams)
    {
        this.timeParams = timeParams;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }
}
