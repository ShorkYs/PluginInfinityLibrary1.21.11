package com.infinitylibrary.library.book;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BookRenderer {
    private static final int MAX_LINES_PER_PAGE = 13;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public void open(Player player, String title, String author, List<BookPage> pages) {
        List<Component> rendered = pages.stream().map(BookPage::toComponent).toList();
        player.openBook(Book.book(Component.text(title), Component.text(author), rendered));
    }

    public List<BookPage> splitLines(List<Component> lines) {
        List<BookPage> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += MAX_LINES_PER_PAGE) {
            pages.add(new BookPage(lines.subList(i, Math.min(lines.size(), i + MAX_LINES_PER_PAGE))));
        }
        return pages;
    }

    public Component routeLink(String label, String route, String hover) {
        return miniMessage.deserialize(label)
                .clickEvent(ClickEvent.runCommand("/library route " + route))
                .hoverEvent(HoverEvent.showText(miniMessage.deserialize(hover)));
    }
}
