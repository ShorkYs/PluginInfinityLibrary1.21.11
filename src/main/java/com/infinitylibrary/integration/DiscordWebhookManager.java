package com.infinitylibrary.integration;

import com.infinitylibrary.InfinityLibraryPlugin;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookManager {
    private final InfinityLibraryPlugin plugin;
    public DiscordWebhookManager(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    public void sendAdded(String title, String desc) { send("🟢 + " + title, desc, 0x2ECC71); }
    public void sendRemoved(String title, String desc) { send("🔴 - " + title, desc, 0xE74C3C); }
    public void sendChanged(String title, String desc) { send("🟠 x " + title, desc, 0xF39C12); }

    private void send(String title, String desc, int color) {
        String url = plugin.getConfig().getString("discord.webhook-url", "");
        if (url.isBlank()) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                String payload = "{\"embeds\":[{\"title\":\"" + escape(title) + "\",\"description\":\"" + escape(desc) + "\",\"color\":" + color + "}]}";
                try (OutputStream os = conn.getOutputStream()) { os.write(payload.getBytes(StandardCharsets.UTF_8)); }
                conn.getInputStream().close();
            } catch (Exception ignored) { }
        });
    }

    private String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"); }
}
