package com.infinitylibrary.rental.npc;

import com.infinitylibrary.InfinityLibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RentalNpcManager {
    private final InfinityLibraryPlugin plugin;
    private final File file;
    private final NamespacedKey npcKey;
    private UUID npcId;

    public RentalNpcManager(InfinityLibraryPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "rental-npc.yml");
        this.npcKey = new NamespacedKey(plugin, "library_npc");
    }

    public void load() {
        if (!file.exists()) return;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        String id = y.getString("npc-id");
        if (id != null && !id.isBlank()) npcId = UUID.fromString(id);
    }

    public void save() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("npc-id", npcId == null ? "" : npcId.toString());
        try { y.save(file); } catch (IOException ignored) { }
    }

    public void placeOrMove(Location location) {
        remove();
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(ChatColor.LIGHT_PURPLE + "Library Assistant");
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.getPersistentDataContainer().set(npcKey, PersistentDataType.BYTE, (byte) 1);
        npcId = villager.getUniqueId();
        save();
    }

    public void remove() {
        Entity npc = getNpc();
        if (npc != null) npc.remove();
        npcId = null;
        save();
    }

    public Entity getNpc() {
        if (npcId == null) return null;
        for (var world : Bukkit.getWorlds()) {
            Entity e = world.getEntity(npcId);
            if (e != null) return e;
        }
        return null;
    }

    public boolean isLibraryNpc(Entity entity) {
        return entity != null && entity.getPersistentDataContainer().has(npcKey, PersistentDataType.BYTE);
    }
}
