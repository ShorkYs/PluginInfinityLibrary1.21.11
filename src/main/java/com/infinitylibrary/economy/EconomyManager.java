package com.infinitylibrary.economy;

import com.infinitylibrary.InfinityLibraryPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {
    private final InfinityLibraryPlugin plugin;
    private Economy economy;

    public EconomyManager(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    public void hook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) economy = rsp.getProvider();
    }

    public boolean available() { return economy != null; }
    public double balance(Player player) { return economy == null ? 0.0 : economy.getBalance(player); }
    public boolean withdraw(Player player, double amount) { return economy != null && economy.withdrawPlayer(player, amount).transactionSuccess(); }
    public String format(double amount) { return economy == null ? String.format("%.2f", amount) : economy.format(amount); }
}
