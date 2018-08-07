package dev.blackcat.minauta.sync;

public abstract class SyncRunnable<T> implements Runnable
{
    private T result = null;

    public T getResult()
    {
        return result;
    }

    public void setResult(T result)
    {
        this.result = result;
    }

    public T execute()
    {
        return SyncThread.execute(this);
    }
}
