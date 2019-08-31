package com.gmail.thetoppe5.clans.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class UpdateNotifier implements Listener {

    private final JavaPlugin plugin;
    private final String API_URL;
    private final String UPDATES_URL;
    private boolean updateAvailable;

    public UpdateNotifier(JavaPlugin plugin, int resourceId, boolean notifyStaff) {
        this.plugin = plugin;
        API_URL = "https://api.spigotmc.org/legacy/update.php?resource=" + resourceId + "/";
        UPDATES_URL = "https://www.spigotmc.org/resources/" + resourceId + "/updates";
        if (notifyStaff) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        checkUpdateAsync();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && updateAvailable) {
            e.getPlayer().sendMessage(ChatColor.GRAY + "There is a new update available for " + ChatColor.AQUA + plugin.getDescription().getName() + ChatColor.GRAY + ".");
            e.getPlayer().sendMessage(ChatColor.GRAY + "Link: " + UPDATES_URL);
        }
    }

    public void checkUpdateAsync() {
        new BukkitRunnable() {

            @Override
            public void run() {
                checkUpdate();
            }
        }.runTaskAsynchronously(plugin);
    }

    public boolean checkUpdate() {
        try {URLConnection conn = new URL(API_URL).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String str1;
            while ((str1 = reader.readLine()) != null) {
                builder.append(str1);
            }
            String str2 = builder.toString();
            if (!plugin.getDescription().getVersion().equals(str2)) {
                updateAvailable = true;
            }
            Bukkit.getLogger().info("There is a new update available for " + plugin.getDescription().getName());
            Bukkit.getLogger().info("Link: " + UPDATES_URL);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to check updates for " + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
        }
        return updateAvailable;
    }

    public boolean wasUpdateAvailable() {
        return updateAvailable;
    }
}
