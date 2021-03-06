package me.stefan456789.claimedsign.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SerializableProtectedRegion implements Serializable {
    private transient Player owner;
    private final String ownerUUID;
    private final ArrayList<String> guestNames = new ArrayList<>();
    private final double x, y, z;
    private final double radius;
    private final String worldName;
    private final UUID worldUUID;
    private transient Location location = null;

    public SerializableProtectedRegion(final Player owner, final Location location, final double radius) {
        this.owner = owner;
        this.ownerUUID = owner.getUniqueId().toString();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.radius = radius;
        this.worldName = location.getWorld().getName();
        this.worldUUID = location.getWorld().getUID();
        this.location = location;
    }

    public Location getLocation() {
        if (this.location == null) {
            World world = Bukkit.getWorld(this.worldUUID);

            if (world == null) {
                world = Bukkit.getWorld(this.worldName);
            }


            location = new Location(world, x, y, z);
        }
        return this.location;
    }

    public double getRadius() {
        return radius;
    }

    public Player getOwner() {

        if (this.owner == null) {
            this.owner = Bukkit.getPlayer(this.ownerUUID);
        }
        if (this.owner == null) {
            this.owner = Bukkit.getOfflinePlayer(UUID.fromString(this.ownerUUID)).getPlayer();
        }
        return this.owner;
    }

    public void addGuest(String player) {
        guestNames.add(player);
    }

    public List<String> getGuests() {
        return guestNames;
    }

    @Override
    public String toString() {
        if (guestNames.size() == 0)
            return "Owner:" + owner + ", at " + x + " " + y + " " + z + " with radius " + radius;
        StringBuilder guestList = new StringBuilder();
        for (String s : guestNames)
            guestList.append(s).append(", ");
        return "Owner:" + owner + ", members:" + guestList + "at " + x + " " + y + " " + z + " with radius " + radius;
    }
}
