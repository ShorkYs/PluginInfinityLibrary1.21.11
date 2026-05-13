package com.infinitylibrary.listener;

import com.infinitylibrary.InfinityLibraryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.entity.Player;

public class RentalNpcListener implements Listener {
    private final InfinityLibraryPlugin plugin;
    public RentalNpcListener(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    @EventHandler public void onNpcInteract(PlayerInteractAtEntityEvent event) {
        if (!plugin.getRentalNpcManager().isLibraryNpc(event.getRightClicked())) return;
        event.setCancelled(true);
        plugin.getGuiManager().openLibraryProfile(event.getPlayer());
    }

    @EventHandler public void onNpcHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!plugin.getRentalNpcManager().isLibraryNpc(event.getEntity())) return;
        event.setCancelled(true);
        plugin.getGuiManager().openBookSearchMenu(player);
    }
}
