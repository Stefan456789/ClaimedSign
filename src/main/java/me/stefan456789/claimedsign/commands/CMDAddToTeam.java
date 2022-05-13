package me.stefan456789.claimedsign.commands;

import me.stefan456789.claimedsign.ClaimedSign;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CMDAddToTeam implements CommandExecutor, TabCompleter {

    private ClaimedSign main;

    public CMDAddToTeam(ClaimedSign main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        main.joinArea(main.getServer().getPlayer(sender.getName()), args[0]);
        sender.sendMessage("Added " + args[0] + " to all existing signs owned by you.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> l = new ArrayList<>();
        for (OfflinePlayer p : main.getServer().getOfflinePlayers())
            l.add(p.getName());
        return l;
    }
}
