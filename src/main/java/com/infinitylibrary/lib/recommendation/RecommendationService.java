package com.infinitylibrary.lib.recommendation;

import com.infinitylibrary.lib.model.LibraryEntry;

import java.util.Comparator;
import java.util.List;

public class RecommendationService {
    public List<LibraryEntry> trending(List<LibraryEntry> entries, int limit) {
        return entries.stream().sorted(Comparator.comparingLong((LibraryEntry e) -> e.views() + e.likes() * 3).reversed()).limit(limit).toList();
    }
}
