package com.infinitylibrary.lib.search;

import com.infinitylibrary.lib.model.LibraryEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LibrarySearchService {
    private final Map<String, List<LibraryEntry>> cache = new ConcurrentHashMap<>();

    public List<LibraryEntry> search(List<LibraryEntry> entries, String query) {
        String normalized = query.toLowerCase(Locale.ROOT).trim();
        return cache.computeIfAbsent(normalized, key -> entries.stream()
                .filter(entry -> matches(entry, key))
                .sorted(Comparator.comparingLong(LibraryEntry::views).reversed())
                .toList());
    }

    public void clearCache() {
        cache.clear();
    }

    private boolean matches(LibraryEntry entry, String query) {
        return fuzzyContains(entry.title(), query)
                || fuzzyContains(entry.author(), query)
                || fuzzyContains(entry.category(), query)
                || entry.tags().stream().anyMatch(tag -> fuzzyContains(tag, query))
                || entry.pages().stream().anyMatch(page -> fuzzyContains(page, query));
    }

    private boolean fuzzyContains(String text, String query) {
        String value = text == null ? "" : text.toLowerCase(Locale.ROOT);
        if (value.contains(query)) return true;
        return levenshtein(value, query) <= 2;
    }

    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
