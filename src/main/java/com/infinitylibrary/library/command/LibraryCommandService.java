package com.infinitylibrary.library.command;

import com.infinitylibrary.InfinityLibraryPlugin;
import com.infinitylibrary.library.book.BookRenderer;
import com.infinitylibrary.library.book.BookSession;
import com.infinitylibrary.library.model.LibraryEntry;
import com.infinitylibrary.library.route.RouteRegistry;
import com.infinitylibrary.library.search.LibrarySearchService;
import com.infinitylibrary.storage.BookStorageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryCommandService {
    private final InfinityLibraryPlugin plugin;
    private final BookRenderer renderer = new BookRenderer();
    private final LibrarySearchService searchService = new LibrarySearchService();
    private final RouteRegistry routeRegistry = new RouteRegistry();
    private final Map<UUID, BookSession> sessions = new ConcurrentHashMap<>();

    public LibraryCommandService(InfinityLibraryPlugin plugin) {
        this.plugin = plugin;
        routeRegistry.register("home", (player, session) -> openHome(player));
    }

    public void openHome(Player player) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.text("§5Infinity Library Wiki"));
        lines.add(renderer.routeLink("<gold>[Trending]</gold>", "trending", "<gray>Top books this week</gray>"));
        lines.add(renderer.routeLink("<aqua>[Recommended]</aqua>", "recommended", "<gray>Readers also liked</gray>"));
        lines.add(Component.text("§7Use /library search <query>"));
        renderer.open(player, "Library", "Archivist", renderer.splitLines(lines));
    }

    public void route(Player player, String route) {
        BookSession session = sessions.computeIfAbsent(player.getUniqueId(), BookSession::new);
        session.pushRoute(route);
        if ("trending".equalsIgnoreCase(route) || "recommended".equalsIgnoreCase(route)) {
            String title = "trending".equalsIgnoreCase(route) ? "Trending this week" : "Recommended for you";
            openEntries(player, title, rankedEntries(20));
            return;
        }
        routeRegistry.resolve(route, player, session);
    }

    public void search(Player player, String query) {
        openEntries(player, "Search: " + query, searchService.search(entries(), query));
    }

    private void openEntries(Player player, String title, List<LibraryEntry> entries) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.text("§d" + title));
        int index = 1;
        for (LibraryEntry entry : entries.stream().limit(40).toList()) {
            lines.add(Component.text("§f" + index++ + ". §5" + entry.title() + " §7by §f" + entry.author()));
            lines.add(Component.text("§8#" + entry.category() + " §7views:" + entry.views() + " likes:" + entry.likes()));
        }
        if (entries.isEmpty()) lines.add(Component.text("§7No matching entries."));
        lines.add(renderer.routeLink("<red>[Home]</red>", "home", "<gray>Return to root</gray>"));
        renderer.open(player, "Library", "Archivist", renderer.splitLines(lines));
    }

    private List<LibraryEntry> rankedEntries(int limit) {
        return entries().stream()
                .sorted(Comparator.comparingLong((LibraryEntry e) -> e.views() + (e.likes() * 3)).reversed())
                .limit(limit)
                .toList();
    }

    private List<LibraryEntry> entries() {
        BookStorageManager storage = plugin.getBookStorageManager();
        return storage.allBooks().stream().map(this::fromStored).toList();
    }

    private LibraryEntry fromStored(BookStorageManager.StoredBook book) {
        String tagLine = book.tags() == null ? "" : book.tags();
        List<String> tags = tagLine.isBlank() ? List.of() : Arrays.stream(tagLine.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
        Instant createdAt;
        try {
            createdAt = Instant.parse(book.insertedAt());
        } catch (Exception ignored) {
            createdAt = Instant.now();
        }
        return new LibraryEntry(book.id(), book.title(), book.author(), book.category().isBlank() ? "uncategorized" : book.category(), tags, book.pages(), "", createdAt, 0, 0);
    }
}
