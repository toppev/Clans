package com.gmail.thetoppe5.clans.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SerializableLocation implements Serializable, ConfigurationSerializable {

    private static final long serialVersionUID = -3174227815222499224L;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String world;

    public SerializableLocation(Location l) {
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.yaw = l.getYaw();
        this.pitch = l.getPitch();
        this.world = l.getWorld().getName();
    }

    public SerializableLocation(double x, double y, double z, float yaw, float pitch, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public Location toLocation() {
        World w = Bukkit.getServer().getWorld(this.world);
        return new Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public static SerializableLocation fromString(String s) {
        String[] ss = s.split(", ");
        double x = Double.parseDouble(ss[0]);
        double y = Double.parseDouble(ss[1]);
        double z = Double.parseDouble(ss[2]);
        float yaw = Float.parseFloat(ss[3]);
        float pitch = Float.parseFloat(ss[4]);
        return new SerializableLocation(x, y, z, yaw, pitch, ss[5]);
    }

    public String toString() {
        return this.x + ", " + this.y + ", " + this.z + ", " + this.yaw + ", " + this.pitch + ", " + this.world;
    }

    public String toReadableString() {
        return round(this.x, 2) + ", " + round(this.y, 2) + ", " + round(this.z, 2) + " @ " + this.world;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> location = new HashMap<String, Object>();
        location.put("x", Double.valueOf(this.x));
        location.put("y", Double.valueOf(this.y));
        location.put("z", Double.valueOf(this.z));
        location.put("yaw", Float.valueOf(this.yaw));
        location.put("pitch", Float.valueOf(this.pitch));
        location.put("world", this.world);
        return location;
    }

    public SerializableLocation(Map<String, Object> location) {
        this.x = parseDouble(location.get("x"));
        this.y = parseDouble(location.get("y"));
        this.z = parseDouble(location.get("z"));
        this.yaw = parseFloat(location.get("yaw"));
        this.pitch = parseFloat(location.get("pitch"));
        this.world = ((String) location.get("world"));
    }

    public SerializableLocation valueOf(Map<String, Object> location) {
        return new SerializableLocation(location);
    }

    public SerializableLocation deserialize(Map<String, Object> location) {
        return new SerializableLocation(location);
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double parseDouble(Object o) {
        if ((o instanceof Double))
            return ((Double) o).doubleValue();
        return Double.parseDouble(o.toString());
    }

    private float parseFloat(Object o) {
        if ((o instanceof Float))
            return ((Float) o).floatValue();
        return Float.parseFloat(o.toString());
    }
}