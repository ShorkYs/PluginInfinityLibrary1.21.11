package com.infinitylibrary.library.route;

import com.infinitylibrary.library.book.BookSession;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RouteRegistry {
    private final Map<String, BiConsumer<Player, BookSession>> handlers = new HashMap<>();

    public void register(String path, BiConsumer<Player, BookSession> handler) { handlers.put(path, handler); }

    public boolean resolve(String path, Player player, BookSession session) {
        BiConsumer<Player, BookSession> handler = handlers.get(path);
        if (handler == null) return false;
        handler.accept(player, session);
        return true;
    }
}
