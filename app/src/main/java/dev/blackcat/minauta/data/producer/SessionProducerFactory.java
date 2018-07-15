package dev.blackcat.minauta.data.producer;

public class SessionProducerFactory
{

    public enum Type
    {
        FAKE,
        JNAUTA
    }

    public static SessionProducer createSessionProducer(Type type)
    {
        return new FakeSessionProducer();
    }
}
