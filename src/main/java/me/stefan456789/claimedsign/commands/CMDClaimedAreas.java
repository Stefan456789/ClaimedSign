package me.stefan456789.claimedsign.commands;

import me.stefan456789.claimedsign.ClaimedSign;
import me.stefan456789.claimedsign.util.SerializableProtectedRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CMDClaimedAreas implements CommandExecutor, TabCompleter {

    private ClaimedSign main;

    public CMDClaimedAreas(ClaimedSign main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player owner = main.getServer().getPlayer(sender.getName());
        if (args.length == 2 && args[1].equals("removeAll")) {
            if (!sender.isOp()) {
                sender.sendMessage("You don't have permission to use this command!");
                return true;
            }
            if (args[0].equals("all"))
                main.clearProtectedAreas();
            else if (args[0].equals("current"))
                main.removeProtectedArea(main.getProtectedAreaOwner(owner.getLocation()));
            else {
                if (args[0].equals(owner.getDisplayName()))
                    main.removeProtectedArea(owner);
            }
            return true;
        } else if (args[0].equals("current")) {
            SerializableProtectedRegion r = main.getProtectedArea(owner.getLocation());

            sender.sendMessage(r.toString());
        }


        for (SerializableProtectedRegion r : main.getProtectedAreas()) {
            if (!(args.length == 0 || args[0].toLowerCase().equals("all")) && !args[0].equals(r.getOwner().getDisplayName()))
                continue;
            sender.sendMessage(r.toString());
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options;


        if (args.length == 1) {
            options = new ArrayList<>();
            for (SerializableProtectedRegion r : main.getProtectedAreas()) {
                options.add(r.getOwner().getDisplayName());
            }
            options.add(0, "all");
            options.add(1, "current");
            options.removeIf(guess -> !guess.startsWith(args[0]));
        } else if (args.length == 2 && !args[0].equals("all"))
            options = Collections.singletonList("removeAll");
        else options = Collections.emptyList();
        return options;
    }
}
