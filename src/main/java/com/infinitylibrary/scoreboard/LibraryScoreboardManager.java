package com.infinitylibrary.scoreboard;

import com.infinitylibrary.InfinityLibraryPlugin;
import com.infinitylibrary.model.PlacedRoom;
import com.infinitylibrary.model.Room;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Optional;

public class LibraryScoreboardManager {
    private final InfinityLibraryPlugin plugin;
    private int task = -1;

    public LibraryScoreboardManager(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    public void start() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 40L, 40L);
    }

    public void stop() { if (task != -1) Bukkit.getScheduler().cancelTask(task); }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) render(player);
    }

    public void render(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("infinitylib", "dummy", ChatColor.DARK_PURPLE + "Infinity Library");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Optional<PlacedRoom> room = plugin.getGenerationEngine().roomAt(player.getLocation());
        String roomId = room.map(PlacedRoom::roomId).orElse("none");
        String type = room.flatMap(r -> plugin.getRoomManager().get(r.roomId())).map(Room::type).map(Enum::name).orElse("NONE");
        String money = plugin.getEconomyManager().format(plugin.getEconomyManager().balance(player));
        objective.getScore(ChatColor.LIGHT_PURPLE + "Room: " + ChatColor.WHITE + roomId).setScore(4);
        objective.getScore(ChatColor.GRAY + "Type: " + ChatColor.WHITE + type).setScore(3);
        objective.getScore(ChatColor.GOLD + "Money: " + ChatColor.WHITE + money).setScore(2);
        objective.getScore(ChatColor.AQUA + "Books: " + ChatColor.WHITE + plugin.getBookStorageManager().playerCount(player.getUniqueId())).setScore(1);
        player.setScoreboard(board);
    }
}
