package me.stefan456789.claimedsign;

import me.stefan456789.claimedsign.commands.CMDAddToTeam;
import me.stefan456789.claimedsign.commands.CMDClaimedAreas;
import me.stefan456789.claimedsign.eventhandlers.EventHandlers;
import me.stefan456789.claimedsign.util.SerializableProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class ClaimedSign extends JavaPlugin {

    private final Set<SerializableProtectedRegion> areas = new HashSet<>();
    public static final String DATA_FILENAME = "protectedareas.dat";
    public static ClaimedSign main;

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getConsoleSender().sendMessage("[ClaimedSign] 1.1 plugin loaded!");
        main = this;

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File input = new File(this.getDataFolder(), DATA_FILENAME);
        try {
            if (input.exists()) {
                FileInputStream fis = new FileInputStream(input);
                ObjectInputStream ois = new ObjectInputStream(fis);
                areas.clear();
                areas.addAll((HashSet<SerializableProtectedRegion>) ois.readObject());
                ois.close();
                fis.close();
            } else
                input.createNewFile();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


        Bukkit.getServer().getPluginManager().registerEvents(new EventHandlers(this), this);


        CMDClaimedAreas caHandler = new CMDClaimedAreas(this);

        getCommand("claimedAreas").setExecutor(caHandler);
        getCommand("claimedAreas").setTabCompleter(caHandler);


        CMDAddToTeam attHandler = new CMDAddToTeam(this);

        getCommand("addToTeam").setExecutor(attHandler);
        getCommand("addToTeam").setTabCompleter(attHandler);
    }

    @Override
    public void onDisable() {
        save();
    }


    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(this.getDataFolder(), DATA_FILENAME));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(areas);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getProtectedAreaAllowed(Location location) {
        List<String> l = new ArrayList<>();
        Player owner = getProtectedAreaOwner(location);
        if (owner == null) return Collections.emptyList();
        l.add(owner.getName());

        for (SerializableProtectedRegion r : areas)
            if (r.getOwner().equals(owner))
                l.addAll(r.getGuests());

        return l;
    }

    public Player getProtectedAreaOwner(Location location) {
        for (SerializableProtectedRegion r : areas) {
            if (r.getLocation().getWorld().equals(location.getWorld())) {
                if (calcIntersectOneAxis(r.getLocation().getX(), r.getRadius(), location.getX()))
                    continue;

                if (calcIntersectOneAxis(r.getLocation().getZ(), r.getRadius(), location.getZ()))
                    continue;

                //if (calcIntersectOneAxis(r.getLocation().getZ(), r.getRadius(), location.getZ()))
                //    continue;


            } else continue;
            return r.getOwner();
        }
        return null;
    }


    private boolean calcIntersectOneAxis(double center, double radius, double playerPos) {
        return center - radius > playerPos || center + radius < playerPos;
    }

    public void addProtectedArea(Player owner, Location location, double radius) {
        if (owner == null) {
            System.out.println("New owner can't be null");
            return;
        }
        areas.add(new SerializableProtectedRegion(owner, location, radius));
        save();
    }

    public void removeProtectedArea(Location location, Player breaker) {
        areas.removeIf((r) -> {
            List<String> allowed = r.getGuests();
            allowed.add(r.getOwner().getName());
            boolean remove = r.getLocation().equals(location) && allowed.contains(breaker.getName());
            if (remove)
                breaker.sendMessage("Claimed area removed!");

            return remove;
        });
        save();
    }

    public void clearProtectedAreas() {
        areas.clear();
        save();
    }

    public Set<SerializableProtectedRegion> getProtectedAreas() {
        return areas;
    }

    public void joinArea(Player owner, Player guest) {
        for (SerializableProtectedRegion r : areas) {
            if (r.getOwner().equals(owner)) {
                r.addGuest(guest.getName());
            }
        }
    }
}
