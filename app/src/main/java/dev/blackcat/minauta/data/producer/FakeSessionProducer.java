package dev.blackcat.minauta.data.producer;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.Session;

public class FakeSessionProducer implements SessionProducer
{
    @Override
    public Session login(Account account)
    {
        return null;
    }

    @Override
    public boolean logout(Session session)
    {
        return false;
    }
}
