package com.infinitylibrary.lib.book;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class BookSession {
    private final UUID playerId;
    private final Deque<String> history = new ArrayDeque<>();

    public BookSession(UUID playerId) { this.playerId = playerId; }
    public UUID playerId() { return playerId; }
    public void pushRoute(String route) { history.push(route); }
    public String popRoute() { return history.isEmpty() ? "home" : history.pop(); }
}
