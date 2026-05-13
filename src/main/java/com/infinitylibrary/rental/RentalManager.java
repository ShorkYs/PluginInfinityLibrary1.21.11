package com.infinitylibrary.rental;

import com.infinitylibrary.InfinityLibraryPlugin;
import com.infinitylibrary.model.PlacedRoom;
import com.infinitylibrary.model.Room;
import com.infinitylibrary.model.RoomType;
import com.infinitylibrary.model.Vector3i;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RentalManager {
    private final InfinityLibraryPlugin plugin;
    private final File file;
    private final NamespacedKey keyToken;
    private final Map<UUID, RentalRecord> rentals = new ConcurrentHashMap<>();

    public RentalManager(InfinityLibraryPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "rentals.yml");
        this.keyToken = new NamespacedKey(plugin, "rental_key");
    }

    public void load() { rentals.clear(); }
    public void save() {}

    public ItemStack createRentalKey() {
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = key.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Library Rental Key");
        meta.setLore(List.of(ChatColor.GRAY + "Use /il rent claim in a RENTING room."));
        meta.getPersistentDataContainer().set(keyToken, PersistentDataType.BYTE, (byte) 1);
        key.setItemMeta(meta);
        return key;
    }

    public boolean isRentalKey(ItemStack stack) {
        return stack != null && stack.hasItemMeta() && stack.getItemMeta().getPersistentDataContainer().has(keyToken, PersistentDataType.BYTE);
    }

    public void claim(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!isRentalKey(hand)) throw new IllegalArgumentException("Hold a Library Rental Key in your main hand.");
        Optional<PlacedRoom> placedRoom = plugin.getGenerationEngine().roomAt(player.getLocation());
        if (placedRoom.isEmpty()) throw new IllegalArgumentException("Stand inside a generated room to claim it.");
        Room room = plugin.getRoomManager().get(placedRoom.get().roomId()).orElseThrow();
        if (room.type() != RoomType.RENTING) throw new IllegalArgumentException("This room is not a RENTING room.");
        rentals.put(player.getUniqueId(), new RentalRecord(placedRoom.get().instanceId().toString(), false, new HashSet<>()));
        hand.setAmount(hand.getAmount() - 1);
        spawnStatsHologram(player, placedRoom.get());
    }

    public void invite(Player owner, Player invited) {
        RentalRecord record = rentals.get(owner.getUniqueId());
        if (record == null) throw new IllegalArgumentException("You do not own a rented room.");
        record.invited().add(invited.getUniqueId().toString());
    }

    public void toggleLock(Player owner) {
        RentalRecord record = rentals.get(owner.getUniqueId());
        if (record == null) throw new IllegalArgumentException("You do not own a rented room.");
        record.setLocked(!record.locked());
    }

    private void spawnStatsHologram(Player owner, PlacedRoom pr) {
        Vector3i c = pr.origin().add(new Vector3i(pr.size().x()/2, 2, pr.size().z()/2));
        Location location = new Location(plugin.getGenerationEngine().ensureWorld(), c.x() + 0.5, c.y(), c.z() + 0.5);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setInvisible(true); stand.setMarker(true); stand.setGravity(false);
        stand.setCustomName(ChatColor.LIGHT_PURPLE + owner.getName() + "'s Reading Room | Books: " + plugin.getBookStorageManager().playerCount(owner.getUniqueId()));
        stand.setCustomNameVisible(true);
    }

    private static class RentalRecord {
        private final String roomInstance;
        private boolean locked;
        private final Set<String> invited;
        private RentalRecord(String roomInstance, boolean locked, Set<String> invited) { this.roomInstance = roomInstance; this.locked = locked; this.invited = invited; }
        boolean locked() { return locked; }
        void setLocked(boolean locked) { this.locked = locked; }
        Set<String> invited() { return invited; }
    }
}
