package dev.blackcat.minauta.data.producer;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.Session;

public interface SessionProducer
{
    Session login(Account account);
    boolean logout(Session session);
}
