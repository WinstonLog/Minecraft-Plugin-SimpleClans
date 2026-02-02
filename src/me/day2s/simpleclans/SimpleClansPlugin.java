package me.day2s.simpleclans;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class SimpleClansPlugin extends JavaPlugin {
    
    private static SimpleClansPlugin instance;
    private ClanManager clanManager;
    private FileConfiguration messages;
    private File messagesFile;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Создаем папку плагина
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Инициализация менеджера кланов
        this.clanManager = new ClanManager();
        
        // Загрузка конфигов
        saveDefaultConfig();
        loadMessages();
        
        // Регистрация команды
        getCommand("clan").setExecutor(new ClanCommand());
        
        getLogger().info("SimpleClans успешно запущен!");
    }
    
    @Override
    public void onDisable() {
        // Сохранение данных кланов
        clanManager.saveClans();
        getLogger().info("SimpleClans выключен!");
    }
    
    // Загрузка messages.yml
    private void loadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    // Геттеры
    public static SimpleClansPlugin getInstance() {
        return instance;
    }
    
    public ClanManager getClanManager() {
        return clanManager;
    }
    
    public FileConfiguration getMessages() {
        return messages;
    }
}