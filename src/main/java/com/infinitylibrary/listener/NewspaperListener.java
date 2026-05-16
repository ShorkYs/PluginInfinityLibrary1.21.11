package com.infinitylibrary.listener;

import com.infinitylibrary.InfinityLibraryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class NewspaperListener implements Listener {
    private final InfinityLibraryPlugin plugin;
    public NewspaperListener(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    @EventHandler public void onDeath(PlayerDeathEvent e) { plugin.getNewspaperManager().record("DEATH", e.getPlayer().getName(), e.getDeathMessage() == null ? "died" : e.getDeathMessage()); }
    @EventHandler public void onAdvancement(PlayerAdvancementDoneEvent e) { plugin.getNewspaperManager().record("ACHIEVEMENT", e.getPlayer().getName(), e.getAdvancement().getKey().getKey()); }
    @EventHandler public void onChat(AsyncPlayerChatEvent e) { plugin.getNewspaperManager().record("CHAT", e.getPlayer().getName(), e.getMessage()); }
}
