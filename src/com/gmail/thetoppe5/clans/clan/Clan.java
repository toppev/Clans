package com.gmail.thetoppe5.clans.clan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.thetoppe5.clans.Clans;
import com.gmail.thetoppe5.clans.util.SerializableLocation;

@SerializableAs("Clan")
public class Clan
implements ConfigurationSerializable{


	public static final String CLAN_CHAT = "ToppeClanWarsClanChatKey";
	
	private UUID owner;
	private String name;
	private HashSet<UUID> members = new HashSet<UUID>();
	private HashSet<UUID> invited = new HashSet<UUID>();
	private Location base;
	private Inventory inventory;


	public Clan(UUID owner, String name) {
		this.name = name;
		this.owner = owner;
		this.members.add(owner);
		this.inventory = Bukkit.createInventory(null, 54);
		Clans.getInstance().clans.add(this);
	}

	public Clan(Map<String, Object> serialized) {
		if (serialized == null) return;
		if (serialized.isEmpty()) return;
		if ((serialized.containsKey("name")) && 
				((serialized.get("name") instanceof String))) {
			this.name = ((String)serialized.get("name"));
		}
		if ((serialized.containsKey("owner")) && 
				((serialized.get("owner") instanceof String))) {
			try {
				this.owner = UUID.fromString((String)serialized.get("owner"));
			}
			catch (IllegalArgumentException e) {
				Bukkit.getLogger().warning("INVALID OWNER IN THE CLAN '" + this.name + "'.");
				return;
			}
		}
		if ((serialized.containsKey("members")) && 
				((serialized.get("members") instanceof List))) {
			List<?> mems = (List<?>)serialized.get("members");
			for (Object o : mems) {
				if ((o instanceof String)) {
					try {
						this.members.add(UUID.fromString((String)o));
					} catch (IllegalArgumentException e) {
						Bukkit.getLogger().warning("INVALID MEMBER IN THE CLAN '" + this.name + "'.");
					}
				}
			}
		}
		if ((serialized.containsKey("base")) && 
				((serialized.get("base") instanceof String))) {
			SerializableLocation sLoc = SerializableLocation.fromString((String)serialized.get("base"));
			if (sLoc != null) {
				this.base = sLoc.toLocation();
			}
		}
		setInventory(Bukkit.createInventory(null, 54));
		if ((serialized.containsKey("inventory")) && 
				((serialized.get("inventory") instanceof List))) {
			int slot = 0;
			for(Object o : (List<?>)serialized.get("inventory")) {
				slot++;
				if(o instanceof ItemStack && slot < 54) {
					inventory.setItem(slot, (ItemStack) o);
				}
			}
		}
	}

	public UUID getOwner() {
		return this.owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<UUID> getMembers() {
		return this.members;
	}

	public void setMembers(HashSet<UUID> members) {
		this.members = members;
	}

	public HashSet<UUID> getInvited() {
		return this.invited;
	}

	public void setInvited(HashSet<UUID> invited) {
		this.invited = invited;
	}

	public Location getBase() {
		return this.base;
	}

	public void setBase(Location base) {
		this.base = base;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Map<String, Object> serialize(){
		Map<String, Object> serialized = new HashMap<String, Object>();
		serialized.put("name", getName());
		serialized.put("owner", getOwner().toString());
		if (getInventory() != null) {
			serialized.put("inventory", getInventory().getContents());
		}
		if (getBase() != null) {
			serialized.put("base", new SerializableLocation(getBase()).toString());
		}
		List<String> mems = new ArrayList<String>();
		for(UUID uuid : members) {
			mems.add(uuid.toString());
		}
		serialized.put("members", mems);
		return serialized;
	}

	public static Clan deserialize(Map<String, Object> serialized) {
		return new Clan(serialized);
	}

	public static Clan valueOf(Map<String, Object> serialized) {
		return new Clan(serialized);
	}

	public static List<Clan> getCurrentOnlineClans() {
		List<Clan> clans = new ArrayList<Clan>();
		for (Player pl : Bukkit.getOnlinePlayers()) {
			Clan playerClan = getClan(pl.getUniqueId());
			if (playerClan != null && !clans.contains(playerClan)) {
				clans.add(playerClan);
			}
		}
		return clans;
	}

	public static Clan getClan(UUID uuid) {
		Clans plugin = Clans.getInstance();
		for (Clan c : plugin.clans) {
			if (c.getMembers().contains(uuid)) {
				return c;
			}
		}
		return null;
	}
}