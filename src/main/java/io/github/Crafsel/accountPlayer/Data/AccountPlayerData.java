package io.github.Crafsel.accountPlayer.Data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "player_data")
public class AccountPlayerData {
    @DatabaseField(id = true) // Устанавливаем UUID как первичный ключ
    private UUID uuid;

    @DatabaseField
    @Getter
    @Setter
    private String player;

    @DatabaseField
    @Getter
    @Setter
    private int level;

    @DatabaseField
    @Getter
    @Setter
    private int xpstat;

    @DatabaseField
    @Getter
    @Setter
    private int xpnext;

    // Пустой конструктор для OrmLite
    public AccountPlayerData() {
    }

    public AccountPlayerData(UUID uuid, String player, int level, int xpstat, int xpnext) {
        this.uuid = uuid;
        this.player = player;
        this.level = level;
        this.xpstat = xpstat;
        this.xpnext = xpnext;
    }

}
