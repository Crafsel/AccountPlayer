package io.github.Dalrex.accountPlayer.Commands;

import io.github.Dalrex.accountPlayer.Api.AccountPlayerApi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AccountPlayerCommand implements CommandExecutor {
    private final AccountPlayerApi accountPlayerApi;

    public AccountPlayerCommand(AccountPlayerApi accountPlayerApi) {
        this.accountPlayerApi = accountPlayerApi;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверяем, что команду выполняет игрок
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage("Использование: /accountplayer <действие> [параметры]");
                return false;
            }

            String action = args[0].toLowerCase();  // Получаем действие из первого аргумента

            switch (action) {
                case "getlevel":
                    // Получаем уровень игрока
                    int level = accountPlayerApi.getLevel(player.getUniqueId());
                    player.sendMessage("Ваш уровень: " + level);
                    break;
                case "setlevel":
                    // Устанавливаем уровень игрока
                    if (args.length == 2) {
                        try {
                            int newLevel = Integer.parseInt(args[1]);
                            accountPlayerApi.setLevel(player.getUniqueId(), newLevel);
                            player.sendMessage("Ваш уровень был установлен на: " + newLevel);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Неверный формат уровня. Используйте число.");
                        }
                    } else {
                        player.sendMessage("Использование: /accountplayer setlevel <уровень>");
                    }
                    break;
                case "addxp":
                    // Добавляем XP игроку
                    if (args.length == 2) {
                        try {
                            int xpToAdd = Integer.parseInt(args[1]);
                            accountPlayerApi.updateCurrentXp(player.getUniqueId(), xpToAdd);
                            player.sendMessage("Вам добавлено " + xpToAdd + " XP.");
                        } catch (NumberFormatException e) {
                            player.sendMessage("Неверный формат XP. Используйте число.");
                        }
                    } else {
                        player.sendMessage("Использование: /accountplayer addxp <XP>");
                    }
                    break;
                default:
                    player.sendMessage("Неизвестное действие: " + action);
                    break;
            }
        } else {
            sender.sendMessage("Эту команду можно выполнить только игроку.");
        }

        return true;
    }
}
