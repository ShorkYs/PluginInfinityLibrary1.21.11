package com.infinitylibrary.library.book;

import net.kyori.adventure.text.Component;

import java.util.List;

public record BookPage(List<Component> lines) {
    public Component toComponent() {
        Component page = Component.empty();
        for (Component line : lines) page = page.append(line).appendNewline();
        return page;
    }
}
