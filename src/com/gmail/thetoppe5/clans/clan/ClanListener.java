package com.gmail.thetoppe5.clans.clan;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.gmail.thetoppe5.clans.Clans;

public class ClanListener implements Listener {

    private Clans plugin;

    public ClanListener(Clans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata(Clan.CLAN_CHAT)) {
            Clan clan = Clan.getClan(p.getUniqueId());
            for (UUID uuid : clan.getMembers()) {
                Player mem = Bukkit.getPlayer(uuid);
                if (mem != null) {
                    mem.sendMessage(
                            ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("clan-chat-format")
                                    .replace("<player>", p.getName()).replace("<message>", e.getMessage())));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))) {
            Player tar = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            if (plugin.enabledWorlds.contains(tar.getWorld().getName().toLowerCase())) {
                Clan damagerClan = Clan.getClan(damager.getUniqueId());
                Clan tarClan = Clan.getClan(tar.getUniqueId());
                if ((damagerClan != null) && (tarClan != null) && (damagerClan.getOwner().equals(tarClan.getOwner()))) {
                    e.setCancelled(true);
                    plugin.sendMessage(damager, "clan-member-damage");
                }
            }
        }
    }
}
