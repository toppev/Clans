package com.gmail.thetoppe5.clans.clan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.thetoppe5.clans.Clans;
import com.gmail.thetoppe5.clans.util.DelayedTeleport;
import com.gmail.thetoppe5.clans.util.SerializableLocation;

public class ClanCommand implements CommandExecutor{


	private final Clans plugin;

	
	public ClanCommand(Clans plugin){
		this.plugin = plugin;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (!(sender instanceof Player)) return true;
		Player p = (Player)sender;
		if (args.length == 0) {
			p.sendMessage(ChatColor.BLUE + "/clan create <name>");
			p.sendMessage(ChatColor.BLUE + "/clan invite <player>");
			p.sendMessage(ChatColor.BLUE + "/clan join <player>");
			p.sendMessage(ChatColor.BLUE + "/clan info [player]");
			p.sendMessage(ChatColor.BLUE + "/clan kick");
			p.sendMessage(ChatColor.BLUE + "/clan chest");
			p.sendMessage(ChatColor.BLUE + "/clan setbase");
			p.sendMessage(ChatColor.BLUE + "/clan base");
			p.sendMessage(ChatColor.BLUE + "/clan leave");
			p.sendMessage(ChatColor.BLUE + "/clan chat");
			p.sendMessage(ChatColor.BLUE + "/clan disband");
			return true;
		}
		if (!plugin.enabledWorlds.contains(p.getWorld().getName().toLowerCase())) {
			plugin.sendMessage(p, "not-clan-world");
			return true;
		}
		Clan clan = Clan.getClan(p.getUniqueId());
		String arg = args[0].toLowerCase();
		if(arg.equals("create")) {
			if (clan != null) {
				plugin.sendMessage(p, "already-in-clan");
				return true;
			}
			if (args.length < 2) {
				p.sendMessage(ChatColor.BLUE + "/clan create <name>");
				return true;
			}
			String name = args[1];
			for (Clan c : plugin.clans) {
				if (c.getName().equals(name)) {
					plugin.sendMessage(p, "name-taken");
					return true;
				}
			}
			new Clan(p.getUniqueId(), name);
			plugin.sendMessage(p, "clan-created");
		}
		else if(arg.equals("invite")) {
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			if (!clan.getOwner().equals(p.getUniqueId())) {
				plugin.sendMessage(p, "not-clan-owner");
				return true;
			}
			if (args.length < 2) {
				p.sendMessage(ChatColor.BLUE + "/clan invite <player>");
				return true;
			}
			String invited = args[1];
			if (invited.equals(p.getName())) return true;
			Player tar = Bukkit.getPlayer(invited);
			if (tar == null) {
				plugin.sendMessage(p, "not-online");
				return true;
			}
			clan.getInvited().add(tar.getUniqueId());
			plugin.sendMessage(p, "invited");
			tar.sendMessage(plugin.getMessage("invited-you").replace("<player>", p.getName()));
		}
		else if(arg.equals("join")) {
			if (clan != null) {
				plugin.sendMessage(p, "already-in-clan");
				return true;
			}
			if (args.length < 2) {
				p.sendMessage(ChatColor.BLUE + "/clan join <player>");
				return true;
			}
			String targetName = args[1];
			Player clanOwner = Bukkit.getPlayer(targetName);
			if (clanOwner == null) {
				plugin.sendMessage(p, "not-online");
				return true;
			}
			Clan c = Clan.getClan(clanOwner.getUniqueId());
			if (c == null) {
				plugin.sendMessage(p, "does-not-have-a-clan");
				return true;
			}
			if (c.getInvited().contains(p.getUniqueId())) {
				c.getMembers().add(p.getUniqueId());
				c.getInvited().remove(p.getUniqueId());
				for (UUID uuid : c.getMembers()) {
					Player mem = Bukkit.getPlayer(uuid);
					if (mem != null) {
						mem.sendMessage(plugin.getMessage("joined-clan").replace("<player>", p.getName()));
					}
				}
			}
		}
		else if(arg.equals("kick")) {
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			if (!clan.getOwner().equals(p.getUniqueId())) {
				plugin.sendMessage(p, "not-clan-owner");
				return true;
			}
			if (args[1].equals(p.getName())) return true;
			Player tar2 = Bukkit.getPlayer(args[1]);
			if (tar2 == null) {
				plugin.sendMessage(p, "not-online");
				return true; }
			for (UUID uuid : clan.getMembers()) {
				Player mem = Bukkit.getPlayer(uuid);
				if (mem != null) {
					mem.sendMessage(plugin.getMessage("kicked-from-clan").replace("<player>", tar2.getName()));
				}
			}
			clan.getMembers().remove(tar2.getUniqueId());
		}
		else if(arg.equals("inventory") || arg.equals("inv") || arg.equals("chest")) {
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			p.openInventory(clan.getInventory());
		}
		else if(arg.equals("leave")) {
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			if (clan.getOwner().equals(p.getUniqueId())) {
				plugin.sendMessage(p, "disband-to-leave");
				return true;
			}
			String left = plugin.getMessage("has-left-clan").replace("<player>", p.getName());
			for (UUID uuid : clan.getMembers()) {
				Player mem = Bukkit.getPlayer(uuid);
				if (mem != null) {
					mem.sendMessage(left);
				}
			}
			clan.getMembers().remove(p.getUniqueId());
		}
		else if(arg.equals("disband")) {
			String disb = plugin.getMessage("clan-was-disbanded").replace("<player>", p.getName());
			for (UUID uuid : clan.getMembers()) {
				Player mem = Bukkit.getPlayer(uuid);
				if (mem != null) {
					mem.sendMessage(disb);
					mem.closeInventory();
				}
			}
			if (clan.getInventory() != null) {
				for(ItemStack item : clan.getInventory().getContents()) {
					if ((item != null) && (item.getType() != Material.AIR)) {
						p.getWorld().dropItem(p.getLocation(), item);
					}
				}
				plugin.clans.remove(clan);
			}
		}
		else if(arg.equals("chat")) {
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			if (p.hasMetadata(Clan.CLAN_CHAT)) {
				plugin.sendMessage(p, "clan-chat-off");
				p.removeMetadata(Clan.CLAN_CHAT, plugin);
				return true;
			}
			plugin.sendMessage(p, "clan-chat-on");
			p.setMetadata(Clan.CLAN_CHAT, new FixedMetadataValue(plugin, Boolean.valueOf(true)));
		}
		else if(arg.equals("info")) {
			if (args.length < 2) {
				if (clan != null) {
					showClanInfo(p, p.getName());
				}
				else{
					p.sendMessage(ChatColor.BLUE + "/clan info <player>");
				}
				return true;
			}
			else {
				showClanInfo(p, args[1]);
			}
		}
		else if(arg.equals("setbase")){
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			clan.setBase(p.getLocation());
			String s = plugin.getMessage("clan-base-set").replace("<player>", p.getName());
			for (UUID uuid : clan.getMembers()) {
				Player mem = Bukkit.getPlayer(uuid);
				if (mem != null) {
					mem.sendMessage(s);
				}
			}
		}
		else if(arg.equals("base")){
			if (clan == null) {
				plugin.sendMessage(p, "not-in-clan");
				return true;
			}
			if (clan.getBase() == null) {
				plugin.sendMessage(p, "no-clan-base");
				return true;
			}
			plugin.sendMessage(p, "teleporting");
			DelayedTeleport.doDelayedTeleport(plugin, p, clan.getBase());
		}
		return true;
	}

	
	private void showClanInfo(final Player p, final String offlinePlayerName){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			
			@Override
			public void run(){
				@SuppressWarnings("deprecation")
				OfflinePlayer op = Bukkit.getOfflinePlayer(offlinePlayerName);
				final Clan clan = Clan.getClan(op.getUniqueId());
				if (!op.hasPlayedBefore() || clan == null) {
					Bukkit.getScheduler().runTask(plugin, new Runnable(){
						public void run(){
							if (p != null && p.isOnline()) {
								plugin.sendMessage(p, "clan-not-found");
							}
						}
					});
				}
				else {
					HashSet<UUID> uuidMems = clan.getMembers();
					final List<String> mems = new ArrayList<String>();
					for (UUID uuid : uuidMems) {
						OfflinePlayer ofMem = Bukkit.getOfflinePlayer(uuid);
						if(ofMem != null) {
							mems.add(ChatColor.RED + ofMem.getName());
						}
					}
					Bukkit.getScheduler().runTask(plugin, new Runnable(){

						@Override
						public void run(){
							if (p != null) {
								if(clan != null) {
									p.sendMessage(ChatColor.GOLD + "---------------------------------------");
									p.sendMessage(ChatColor.BLUE + "Clan info: " + ChatColor.GREEN + clan.getName());
									p.sendMessage(ChatColor.BLUE + "Members (" + mems.size() + "): " + mems.toString().replace("[", "").replace("]", ""));
									if(plugin.getConfig().getBoolean("show-clan-base")) {
										try{
											p.sendMessage(ChatColor.BLUE + "Base: " + ChatColor.GREEN + new SerializableLocation(clan.getBase()).toReadableString());
										}catch(Exception e){
											p.sendMessage(ChatColor.BLUE + "Base: " + ChatColor.GREEN + "unknown");
										}
									}
									p.sendMessage(ChatColor.GOLD + "---------------------------------------");
								}
							}
						}
					});
				}
			}
		});
	}
}