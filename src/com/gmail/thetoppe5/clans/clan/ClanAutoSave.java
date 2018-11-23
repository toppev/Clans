package com.gmail.thetoppe5.clans.clan;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.thetoppe5.clans.Clans;

public class ClanAutoSave extends BukkitRunnable {

    private Clans plugin;

    public ClanAutoSave(Clans plugin, long delay) {
        runTaskTimerAsynchronously(plugin, delay, delay);
    }

    @Override
    public void run() {
        plugin.saveClans();
    }

}
