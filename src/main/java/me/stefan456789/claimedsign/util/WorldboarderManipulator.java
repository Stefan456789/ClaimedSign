package me.stefan456789.claimedsign.util;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import me.stefan456789.claimedsign.ClaimedSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;


public final class WorldboarderManipulator {

    private static RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider;
    private static WorldBorderApi worldBorderApi;

    private static void init(Server server) {

        if (worldBorderApiRegisteredServiceProvider == null) {
            worldBorderApiRegisteredServiceProvider = server.getServicesManager().getRegistration(WorldBorderApi.class);

            if (worldBorderApiRegisteredServiceProvider == null) {
                server.getLogger().info("[ClaimedSign] World Border API not found");
                return;
            }
        }


        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();
    }

    public static void moveWorldboarderForPlayer(Player player, Location location, int radius) {
        if (worldBorderApiRegisteredServiceProvider == null || worldBorderApi == null)
            init(player.getServer());

        worldBorderApi.setBorder(player, radius * 2 + 1, location);
    }

    public static void resetWorldBoarder(Player player) {
        if (worldBorderApiRegisteredServiceProvider == null || worldBorderApi == null)
            init(player.getServer());

        worldBorderApi.resetWorldBorderToGlobal(player);
    }

    public static void blinkBoarder(Player player, Location location, int radius, long timeoutMillis) {

        if (worldBorderApiRegisteredServiceProvider == null || worldBorderApi == null)
            init(player.getServer());

        Bukkit.getScheduler().runTask(ClaimedSign.main, () -> {
            for (int i = 0; i < 3; i++) {
                try {
                    moveWorldboarderForPlayer(player, location, radius);
                    Thread.sleep(timeoutMillis);
                    resetWorldBoarder(player);
                    Thread.sleep(timeoutMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        resetWorldBoarder(player);


    }

}
