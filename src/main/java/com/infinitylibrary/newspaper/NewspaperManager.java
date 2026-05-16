package com.infinitylibrary.newspaper;

import com.infinitylibrary.InfinityLibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NewspaperManager {
    private final InfinityLibraryPlugin plugin;
    private final ConcurrentHashMap<LocalDate, List<String>> dailyEvents = new ConcurrentHashMap<>();

    public NewspaperManager(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    public void record(String eventType, String playerName, String payload) {
        String line = "[" + eventType + "] " + playerName + " - " + payload;
        dailyEvents.computeIfAbsent(LocalDate.now(), d -> new ArrayList<>()).add(line);
        plugin.getDiscordWebhookManager().sendAdded("Daily Newspaper Event", line);
        if (!plugin.getMySqlManager().isEnabled()) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection c = plugin.getMySqlManager().connection(); PreparedStatement ps = c.prepareStatement("INSERT INTO newspaper_events(event_type, player_name, payload) VALUES(?,?,?)")) {
                ps.setString(1, eventType); ps.setString(2, playerName); ps.setString(3, payload); ps.executeUpdate();
            } catch (SQLException ex) { plugin.getLogger().warning("Unable to persist newspaper event: " + ex.getMessage()); }
        });
    }

    public void sendDailyPaper(Player player) {
        List<String> lines = dailyEvents.getOrDefault(LocalDate.now(), List.of());
        player.sendMessage(ChatColor.GOLD + "=== Infinity Daily Newspaper (" + LocalDate.now() + ") ===");
        if (lines.isEmpty()) { player.sendMessage(ChatColor.GRAY + "No stories have been recorded yet today."); return; }
        lines.stream().limit(10).forEach(line -> player.sendMessage(ChatColor.LIGHT_PURPLE + "• " + ChatColor.WHITE + line));
    }
}
