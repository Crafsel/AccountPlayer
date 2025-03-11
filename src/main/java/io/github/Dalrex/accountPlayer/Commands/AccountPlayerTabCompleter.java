package io.github.Dalrex.accountPlayer.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountPlayerTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // Проверяем, что команду выполняет игрок
        if (sender instanceof Player) {
            if (args.length == 1) {
                // Если первый аргумент, то автодополняем действия (getlevel, setlevel, addxp)
                suggestions.addAll(Arrays.asList("getlevel", "setlevel", "addxp"));
            } else if (args.length == 2) {
                // Если второй аргумент, то для действий, которые требуют параметров
                if (args[0].equalsIgnoreCase("setlevel") || args[0].equalsIgnoreCase("addxp")) {
                    suggestions.add("/<число>");
                }
            }
        }

        return suggestions;
    }
}
