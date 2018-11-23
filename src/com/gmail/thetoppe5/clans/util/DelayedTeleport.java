package com.gmail.thetoppe5.clans.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.thetoppe5.clans.Clans;

public class DelayedTeleport extends BukkitRunnable {

    public static final String TELEPORT_ACTION = "ToppeClansDelayedTeleport";

    private String player;
    private Location location;
    private Location to;
    private int counter;
    private Clans plugin;

    private DelayedTeleport(Clans plugin, int seconds, Player p, Location to) {
        this.player = p.getName();
        this.location = p.getLocation();
        this.to = to;
        this.plugin = plugin;
        this.counter = seconds;
    }

    @Override
    public void run() {
        Player p = org.bukkit.Bukkit.getPlayer(this.player);
        if (p != null) {
            if (p.getLocation().distance(this.location) < 1.0D) {
                this.counter -= 1;
                if (this.counter == 0) {
                    p.removeMetadata(TELEPORT_ACTION, this.plugin);
                    p.teleport(this.to);
                    cancel();
                }
            } else {
                this.plugin.sendMessage(p, "teleport-cancelled");
                p.removeMetadata(TELEPORT_ACTION, this.plugin);
                cancel();
            }
        } else {
            cancel();
        }
    }

    public static void doDelayedTeleport(Clans plugin, Player p, Location to) {
        DelayedTeleport dt = new DelayedTeleport(plugin, plugin.getConfig().getInt("teleport-delay"), p, to);
        dt.runTaskTimer(plugin, 20L, 20L);
        p.setMetadata(TELEPORT_ACTION, new FixedMetadataValue(plugin, dt));
    }
}
