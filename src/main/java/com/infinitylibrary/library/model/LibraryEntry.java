package com.infinitylibrary.library.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LibraryEntry(
        UUID id,
        String title,
        String author,
        String category,
        List<String> tags,
        List<String> pages,
        String description,
        Instant createdAt,
        long views,
        long likes
) {}
