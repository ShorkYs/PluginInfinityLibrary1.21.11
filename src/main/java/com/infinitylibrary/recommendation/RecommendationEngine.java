package com.infinitylibrary.recommendation;

import com.infinitylibrary.storage.BookStorageManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RecommendationEngine {
    private final BookStorageManager storage;
    private final Map<UUID, Deque<String>> recentlyRead = new ConcurrentHashMap<>();
    private final Map<String, Integer> trending = new ConcurrentHashMap<>();

    public RecommendationEngine(BookStorageManager storage) { this.storage = storage; }

    public void trackViewedBook(UUID playerId, String title) {
        recentlyRead.computeIfAbsent(playerId, k -> new ArrayDeque<>()).addFirst(title);
        while (recentlyRead.get(playerId).size() > 10) recentlyRead.get(playerId).removeLast();
        trending.merge(title, 1, Integer::sum);
    }

    public List<String> generateForYou(UUID playerId, Set<String> favoriteCategories) {
        return storage.allBooks().stream()
                .filter(b -> favoriteCategories.isEmpty() || favoriteCategories.contains(b.category()))
                .map(BookStorageManager.StoredBook::title)
                .distinct().limit(8).toList();
    }

    public List<String> readersAlsoLiked(UUID playerId) {
        Set<String> recent = new HashSet<>(recentlyRead.getOrDefault(playerId, new ArrayDeque<>()));
        return storage.allBooks().stream().map(BookStorageManager.StoredBook::title).filter(t -> !recent.contains(t)).distinct().limit(8).toList();
    }

    public List<String> trendingThisWeek() {
        return trending.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).map(Map.Entry::getKey).limit(8).toList();
    }
}
