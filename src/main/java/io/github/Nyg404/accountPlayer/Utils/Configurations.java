package io.github.Nyg404.accountPlayer.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Configurations {
    private FileConfiguration fileConfiguration;
    private Plugin plugin;  // Добавляем поле для плагина

    public Configurations(Plugin plugin) {
        this.plugin = plugin;  // Сохраняем ссылку на плагин
        // Автоматически сохраняет файл, если его нет
        plugin.saveDefaultConfig();
        this.fileConfiguration = plugin.getConfig(); // Получаем конфиг
    }

    // Метод для получения конфигурации
    public FileConfiguration getConfig() {
        return this.fileConfiguration;
    }

    // Метод для сохранения конфигурации в файл
    public void saveConfig() {
        // Сохраняем конфиг с изменениями
        try {
            fileConfiguration.save(plugin.getDataFolder() + "/config.yml");
        } catch (Exception e) {
            e.printStackTrace(); // Обработка ошибки сохранения
        }
    }


    // Метод для перезагрузки конфигурации
    public void reloadConfig() {
        plugin.reloadConfig();
        this.fileConfiguration = plugin.getConfig();
    }
}
