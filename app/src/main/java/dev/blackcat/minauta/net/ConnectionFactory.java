package dev.blackcat.minauta.net;

public class ConnectionFactory
{

    public enum Type
    {
        FAKE,
        JNAUTA
    }

    public static <T extends Connection> T createSessionProducer(Class<T> clazz)
    {
        try
        {
            T instance = clazz.newInstance();
            return instance;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
