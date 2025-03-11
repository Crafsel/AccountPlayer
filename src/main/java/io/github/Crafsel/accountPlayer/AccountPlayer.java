package io.github.Crafsel.accountPlayer;

import io.github.Crafsel.accountPlayer.Api.AccountPlayerApi;
import io.github.Crafsel.accountPlayer.Commands.AccountPlayerCommand;
import io.github.Crafsel.accountPlayer.Commands.AccountPlayerTabCompleter;
import io.github.Crafsel.accountPlayer.Events.JoinPlayer;
import io.github.Crafsel.accountPlayer.Placeholder.HoldersPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class AccountPlayer extends JavaPlugin {
    private AccountPlayerApi accountPlayerApi;

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            String dbPath = new File(getDataFolder(), "player_data.db").getAbsolutePath();
            accountPlayerApi = AccountPlayerApi.getInstance("jdbc:sqlite:" + dbPath);  // Получаем синглтон с URL базы
            getServer().getPluginManager().registerEvents(new JoinPlayer(), this);  // Регистрируем обработчик событий
        } catch (SQLException e) {
            getLogger().severe("Не удалось подключиться к базе данных: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);  // Выключаем плагин в случае ошибки
        }

        // Регистрация Placeholders, если активирован PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HoldersPlayer(accountPlayerApi).register();
        }

        // командики

        getCommand("accountplayer").setExecutor(new AccountPlayerCommand(accountPlayerApi));
        getCommand("accountplayer").setTabCompleter(new AccountPlayerTabCompleter());
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (accountPlayerApi != null) {
            try {
                accountPlayerApi.close();  // Закрываем соединение с базой данных
            } catch (Exception e) {
                getLogger().warning("Ошибка при закрытии соединения с базой данных: " + e.getMessage());
            }
        }
    }
}
