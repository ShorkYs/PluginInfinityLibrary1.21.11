package com.infinitylibrary.placeholder;

import com.infinitylibrary.InfinityLibraryPlugin;
import com.infinitylibrary.model.PlacedRoom;
import com.infinitylibrary.model.Room;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LibraryPlaceholderExpansion extends PlaceholderExpansion {
    private final InfinityLibraryPlugin plugin;
    public LibraryPlaceholderExpansion(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "infinitylibrary"; }
    @Override public @NotNull String getAuthor() { return "Codex"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        Optional<PlacedRoom> current = plugin.getGenerationEngine().roomAt(player.getLocation());
        if (params.equalsIgnoreCase("room_id")) return current.map(PlacedRoom::roomId).orElse("none");
        if (params.equalsIgnoreCase("room_type")) return current.flatMap(r -> plugin.getRoomManager().get(r.roomId())).map(Room::type).map(Enum::name).orElse("NONE");
        if (params.equalsIgnoreCase("money")) return plugin.getEconomyManager().format(plugin.getEconomyManager().balance(player));
        if (params.equalsIgnoreCase("books_written")) return String.valueOf(plugin.getBookStorageManager().playerCount(player.getUniqueId()));
        return null;
    }
}
