package com.gmail.thetoppe5.clans.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;

import com.gmail.thetoppe5.clans.Clans;

public class DelayedActionListener implements Listener {

    private Clans plugin;

    public DelayedActionListener(Clans plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent e) {
        if ((e.getEntity() instanceof Player)) {
            Player p = (Player) e.getEntity();
            MetadataValue m = this.plugin.getMetadata(p, DelayedTeleport.TELEPORT_ACTION);
            if ((m != null) && (m.value() != null) && ((m.value() instanceof DelayedTeleport))) {
                DelayedTeleport dt = (DelayedTeleport) m.value();
                p.removeMetadata(DelayedTeleport.TELEPORT_ACTION, this.plugin);
                this.plugin.sendMessage(p, "teleport-cancelled");
                dt.cancel();
            }
        }
    }
}