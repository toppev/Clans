package com.gmail.thetoppe5.clans.util;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.thetoppe5.clans.Clans;

public class MessagesFileManager {

    private Clans plugin;
    private YamlConfiguration messagesConfig;
    private File messagesFile;

    public MessagesFileManager(Clans plugin) {
        this.plugin = plugin;
    }

    public void createDefaultFile() {
        this.messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!this.messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(this.messagesFile);
    }

    public YamlConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public File getMessagesFile() {
        return this.messagesFile;
    }
}