package io.github.Dalrex.accountPlayer.Events;

import io.github.Dalrex.accountPlayer.Api.AccountPlayerApi;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.sql.SQLException;

public class JoinPlayer implements Listener {

    

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Player player = event.getPlayer();
        String playername = event.getPlayer().getName();

        try {
            // Получаем экземпляр синглтона
            AccountPlayerApi accountPlayerApi = AccountPlayerApi.getInstance(); 

            // Проверяем, существует ли игрок в базе данных
            if (accountPlayerApi.getPlayer(uuid) == null) {
                accountPlayerApi.addPlayer(uuid, playername, 0, 0, 100);
            } else {
                player.sendMessage("Добро пожаловать в систему! " + playername);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("Ошибка подключения к базе данных.");
        }
    }
}
