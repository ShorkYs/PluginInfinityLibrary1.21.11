package com.infinitylibrary.library.service;

import com.infinitylibrary.InfinityLibraryPlugin;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncExecutor {
    private final InfinityLibraryPlugin plugin;

    public AsyncExecutor(InfinityLibraryPlugin plugin) {
        this.plugin = plugin;
    }

    public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }

    public CompletableFuture<Void> run(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }
}
