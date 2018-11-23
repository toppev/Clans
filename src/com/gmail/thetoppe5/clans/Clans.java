package com.gmail.thetoppe5.clans;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.thetoppe5.clans.clan.Clan;
import com.gmail.thetoppe5.clans.clan.ClanAutoSave;
import com.gmail.thetoppe5.clans.clan.ClanCommand;
import com.gmail.thetoppe5.clans.clan.ClanListener;
import com.gmail.thetoppe5.clans.util.DelayedActionListener;
import com.gmail.thetoppe5.clans.util.MessagesFileManager;

public class Clans extends JavaPlugin {

    private MessagesFileManager fileManager;
    private static Clans instance;
    public HashSet<Clan> clans = new HashSet<Clan>();
    public HashSet<String> enabledWorlds = new HashSet<String>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigurationSerialization.registerClass(Clan.class, "Clan");
        this.fileManager = new MessagesFileManager(this);
        fileManager.createDefaultFile();
        getCommand("clan").setExecutor(new ClanCommand(this));
        Bukkit.getPluginManager().registerEvents(new DelayedActionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ClanListener(this), this);

        File f = new File(getDataFolder(), "clans");
        f.mkdirs();
        for (File file : f.listFiles()) {
            file.getName().endsWith(".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.get("clan") != null) {
                Object o = config.get("clan");
                if ((o instanceof Clan)) {
                    this.clans.add((Clan) o);
                }
            }
        }
        getConfig().getStringList("clan-worlds").forEach(s -> enabledWorlds.add(s.toLowerCase()));
        // config value is in minutes
        long autoSaveDelay = getConfig().getLong("clan-auto-save-period") * 20 * 60;
        if (autoSaveDelay == 0) {
            Bukkit.getLogger().warning(this.getDescription().getName()
                    + "'s clan autosave is disabled. Change 'clan-auto-save-period' to enable it.'");
        } else {
            if (autoSaveDelay >= 5) {
                Bukkit.getLogger().warning("'clan-auto-save-period' should not be less than 5 minutes.");
            }
            new ClanAutoSave(this, autoSaveDelay);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
        saveClans();
    }

    public void saveClans() {
        File f = new File(getDataFolder(), "clans");
        f.mkdirs();
        for (Clan clan : this.clans) {
            File file = new File(getDataFolder(), "clans" + File.separator + clan.getName() + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
            conf.set("clan", clan);
            try {
                conf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Player p, String path) {
        p.sendMessage(getMessage(path));
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("prefix") + getMessagesFileManager().getMessagesConfig().getString(path));
    }

    public MetadataValue getMetadata(Metadatable m, String tag) {
        for (MetadataValue mv : m.getMetadata(tag)) {
            if (mv.getOwningPlugin().equals(this))
                return mv;
        }
        return null;
    }

    public MessagesFileManager getMessagesFileManager() {
        return this.fileManager;
    }

    public static Clans getInstance() {
        return instance;
    }
}