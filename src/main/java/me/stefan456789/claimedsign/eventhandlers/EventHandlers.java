package me.stefan456789.claimedsign.eventhandlers;

import me.stefan456789.claimedsign.ClaimedSign;
import me.stefan456789.claimedsign.util.WorldboarderManipulator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class EventHandlers implements Listener {

    private final ClaimedSign main;


    public EventHandlers(ClaimedSign main) {
        this.main = main;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {


        checkForClaimed(event.getBlock(), event.getPlayer());
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {


        checkForClaimed(event.getBlock(), event.getPlayer());
    }

    private void checkForClaimed(Block block, Player player) {
        final Location location = block.getLocation();
        List<String> allowed = main.getProtectedAreaAllowed(location);
        if (!allowed.isEmpty() && !allowed.contains(player.getName())) {
            player.sendMessage(main.getProtectedAreaOwner(location).getDisplayName() + " already wants to build here!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void createClaimedArea(SignChangeEvent event) {

        if (event.getLine(0).toLowerCase().contains("claim")) {
            int radius = Integer.parseInt(event.getLine(1));
            if (radius > 50) radius = 50;
            main.addProtectedArea(event.getPlayer(), event.getBlock().getLocation(), radius);
            event.setLine(0, event.getPlayer().getDisplayName() + "'s property!");
            event.setLine(1, radius + " block radius.");

            WorldboarderManipulator.blinkBoarder(event.getPlayer(), event.getBlock().getLocation(), radius, 500);
        }


        //Sign test2 = (org.bukkit.block.Sign) event.getPlayer().getWorld().getBlockAt(event.getBlock().getLocation());
        //test2.setLine(1,"test");
    }

    @EventHandler
    public void deleteClaimedArea(BlockBreakEvent event) {
        if (event.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign || event.getBlock().getBlockData() instanceof WallSign) {
            event.setCancelled(main.removeProtectedArea(event.getBlock().getLocation(), event.getPlayer()));

        }
    }

}
