package io.github.Dalrex.accountPlayer.Placeholder;

import static org.bukkit.Bukkit.getLogger;


import java.util.UUID;

import org.bukkit.entity.Player;

import io.github.Dalrex.accountPlayer.Api.AccountPlayerApi;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class HoldersPlayer extends PlaceholderExpansion {
    
    private AccountPlayerApi accountPlayerApi;

    // Инициализация AccountPlayerApi без блока try-catch в конструкторе
    public HoldersPlayer(AccountPlayerApi accountPlayerApi) {
        this.accountPlayerApi = accountPlayerApi;
    }

    @Override
    public String getAuthor() {
        return "Nyg404";
    }

    @Override
    public String getIdentifier() {
        return "AccountPlayer";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        UUID uuid = player.getUniqueId();
        // Внутри этого метода уже не нужно обрабатывать SQLException
        if (accountPlayerApi != null) {
            switch (identifier.toLowerCase()) {
                case "getlevel":
                    return String.valueOf(accountPlayerApi.getLevel(uuid));
                case "getxpstat":
                    return String.valueOf(accountPlayerApi.getCurrentXp(uuid));
                case "getxpnext":
                    return String.valueOf(accountPlayerApi.getXpToNextLevel(uuid));
                default:
                    return null;
            }
        }
        return null;
}


    // Закрытие соединения с базой данных при отключении плагина
    public void close() {
        if (accountPlayerApi != null) {
            try {
                accountPlayerApi.close();
            } catch (Exception e) {
                getLogger().warning("Ошибка при закрытии соединения с базой данных: " + e.getMessage());
            }
        }
    }
}
