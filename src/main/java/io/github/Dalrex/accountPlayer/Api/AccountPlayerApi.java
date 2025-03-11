package io.github.Dalrex.accountPlayer.Api;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.github.Dalrex.accountPlayer.Data.AccountPlayerData;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

public class AccountPlayerApi {
    private static Dao<AccountPlayerData, UUID> playerDao;
    private static ConnectionSource dbConnectionSource;
    private static AccountPlayerApi instance;

    private static String DEFAULT_DATABASE_URL = "jdbc:sqlite:player_data.db";  // URL по умолчанию

    // Приватный конструктор, чтобы нельзя было создать экземпляр напрямую
    private AccountPlayerApi(String dbUrl) throws SQLException {
        dbConnectionSource = new JdbcConnectionSource(dbUrl);
        playerDao = DaoManager.createDao(dbConnectionSource, AccountPlayerData.class);
        TableUtils.createTableIfNotExists(dbConnectionSource, AccountPlayerData.class);
    }

    /**
     * 
     * @return Дефолтный url базы данных (config.yml)
     * @throws SQLException
     */
    public static AccountPlayerApi getInstance() throws SQLException {
        return getInstance(DEFAULT_DATABASE_URL);
    }

    /**
     * 
     * @param dbUrl
     * @return Устанавливаем свой url базы данных (без config.yml)
     * @throws SQLException
     */
    public static AccountPlayerApi getInstance(String dbUrl) throws SQLException {
        if (instance == null) {
            instance = new AccountPlayerApi(dbUrl);
        }
        return instance;
    }

    public void close() throws Exception {
        if (dbConnectionSource != null) {
            dbConnectionSource.close();
            dbConnectionSource = null;
            playerDao = null;
            instance = null;
        }
    }
    /**
     * 
     * @param playerUuid
     * @return Получаем игрока...
     */
    public AccountPlayerData getPlayer(UUID playerUuid) {
        try {
            return playerDao.queryForId(playerUuid);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
        }
        return null;
    }
    /**
     * 
     * @param playerUuid
     * @param playerName
     * @param playerLevel
     * @param currentXp
     * @param xpToNextLevel
     * @return Добавляем игрока в базу данных : Устанавливая значения : Стандарт playerUuuid, playerName, 0, 0, 100
     */
    public boolean addPlayer(UUID playerUuid, String playerName, int playerLevel, int currentXp, int xpToNextLevel) {
        try {
            AccountPlayerData existingPlayer = playerDao.queryForId(playerUuid);
            if (existingPlayer != null) {
                return false;
            }

            AccountPlayerData newPlayer = new AccountPlayerData(playerUuid, playerName, playerLevel, currentXp, xpToNextLevel);
            AccountPlayerData result = playerDao.createIfNotExists(newPlayer);
            return result != null;
        } catch (SQLException e) {
            getLogger().warning("Ошибка при добавлении игрока: " + e.getMessage());
        }
        return false;
    }
    /**
     * 
     * @param playerUuid
     * @return Возвращает левел игрока
     */
    public int getLevel(UUID playerUuid) {
        try {
            AccountPlayerData playerData = playerDao.queryForId(playerUuid);
            if (playerData != null) {
                return playerData.getLevel();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 
     * @param playerUuid
     * @return Возвращает текущие xp игрока
     */
    public int getCurrentXp(UUID playerUuid) {
        try {
            AccountPlayerData playerData = playerDao.queryForId(playerUuid);
            if (playerData != null) {
                return playerData.getXpstat();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 
     * @param playerUuid
     * @return Возвращает требуемое xp для повышения уровня
     */
    public int getXpToNextLevel(UUID playerUuid) {
        try {
            AccountPlayerData playerData = playerDao.queryForId(playerUuid);
            if (playerData != null) {
                return playerData.getXpnext();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Устанавливает левел для игрока.
     * 
     */
    public void setLevel(UUID playerUuid, int newLevel) {
        try {
            AccountPlayerData playerData = playerDao.queryForId(playerUuid);
            if (playerData != null) {

                playerData.setLevel(newLevel);
                playerDao.update(playerData);
            }
        } catch (SQLException e) {
            getLogger().warning("Ошибка при обновлении уровня игрока с UUID " + playerUuid + ": " + e.getMessage());
        }
    }
    
    /**
     * Проверяет, достаточно ли текущего XP для повышения уровня.
     * Если текущий XP (currentXp) больше или равен требуемому порогу (xpToNextLevel),
     * то уровень повышается (на 1 за каждое прохождение порога),
     * а требуемый XP для следующего уровня пересчитывается как xpToNextLevel * 1.1 (округляется вверх).
     * Текущее количество XP при этом остаётся неизменным.
     */
    public void updateLevelIfXpSufficient(UUID playerUuid) {
        try {
            AccountPlayerData playerDataObj = playerDao.queryForId(playerUuid);
            if (playerDataObj != null) {
                if (playerDataObj.getLevel() < 100) {
                    int currentXp = playerDataObj.getXpstat();
                    int xpThreshold = playerDataObj.getXpnext();
                    boolean levelUpdated = false;
    
                    // Продолжаем повышать уровень, пока хватает XP
                    while (currentXp >= xpThreshold && playerDataObj.getLevel() < 100) {
                        // Повышаем уровень на 1
                        playerDataObj.setLevel(playerDataObj.getLevel() + 1);
                        // Пересчитываем XP для следующего уровня (округляем вверх)
                        xpThreshold = (int) Math.ceil(xpThreshold * 1.1);
                        playerDataObj.setXpnext(xpThreshold);
                        levelUpdated = true;
    
                        // Получаем игрока по UUID
                        Player player = Bukkit.getPlayer(playerUuid);
                        if (player != null) {
                            // Отправляем сообщение игроку с актуальным уровнем
                            player.sendMessage(Component.text(ChatColor.GREEN + "Поздравляем! Ваш уровень повысился!" + " Ваш левел: " + ChatColor.DARK_BLUE + playerDataObj.getLevel()));
                        }
                    }
    
                    // Если уровень был обновлён, сохраняем изменения
                    if (levelUpdated) {
                        playerDao.update(playerDataObj);
                    }
                }
            }
        } catch (SQLException e) {
            getLogger().warning("Ошибка при обновлении уровня игрока с UUID " + playerUuid + ": " + e.getMessage());
        }
    }

    


    /**
     * 
     * Устанавливаем текущие количество xp у игрока
     * 
     * @param playerUuid
     * @param newCurrentXp
     * 
     */
    public void setCurrentXp(UUID playerUuid, int newCurrentXp) {
        try {
            AccountPlayerData playerDataObj = playerDao.queryForId(playerUuid);
            if (playerDataObj != null) {
                playerDataObj.setXpstat(newCurrentXp);
                playerDao.update(playerDataObj);
                updateLevelIfXpSufficient(playerUuid);
            }
        } catch (SQLException e) {
            getLogger().warning("Ошибка при обновлении текущего XP игрока с UUID " + playerUuid + ": " + e.getMessage());
        }
    }

    /**
     * 
     * Прибавляем xp к игроку xp 
     * 
     * @param playerUuid
     * @param xpIncrement
     */
    public void updateCurrentXp(UUID playerUuid, int xpIncrement) {
        try {
            AccountPlayerData playerDataObj = playerDao.queryForId(playerUuid);
            if (playerDataObj != null) {
                int currentXp = playerDataObj.getXpstat();
                playerDataObj.setXpstat(currentXp + xpIncrement);
                playerDao.update(playerDataObj);
                updateLevelIfXpSufficient(playerUuid);
            }
        } catch (SQLException e) {
            getLogger().warning("Ошибка при обновлении текущего XP игрока с UUID " + playerUuid + ": " + e.getMessage());
        }
    }
    /**
     * 
     * Устанавливаем требуемое количество xp для повышения уровня
     * 
     * @param playerUuid
     * @param newXpToNextLevel
     */
    public void setXpToNextLevel(UUID playerUuid, int newXpToNextLevel) {
        try {
            AccountPlayerData playerDataObj = playerDao.queryForId(playerUuid);
            if (playerDataObj != null) {
                playerDataObj.setXpnext(newXpToNextLevel);
                playerDao.update(playerDataObj);
            }
        } catch (SQLException e) {
            getLogger().warning("Ошибка при обновлении XP до следующего уровня для игрока с UUID " + playerUuid + ": " + e.getMessage());
        }
    }
}
