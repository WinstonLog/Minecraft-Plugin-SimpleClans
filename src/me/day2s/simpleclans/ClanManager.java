package me.day2s.simpleclans;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ClanManager {
    private Map<String, Clan> clans = new HashMap<>();
    private Map<UUID, String> playerClans = new HashMap<>();
    private File clansFile;
    private FileConfiguration clansConfig;
    
    public ClanManager() {
        loadClans();
    }
    
    // Создание клана
    public boolean createClan(Player player, String name) {
        if (hasClan(player)) {
            MessageUtils.sendMessage(player, "clan.already-have");
            return false;
        }
        
        if (clans.containsKey(name.toLowerCase())) {
            MessageUtils.sendMessage(player, "clan.exists");
            return false;
        }
        
        Clan clan = new Clan(name, player.getUniqueId());
        clans.put(name.toLowerCase(), clan);
        playerClans.put(player.getUniqueId(), name.toLowerCase());
        
        MessageUtils.sendMessage(player, "clan.created", 
            "{name}", name);
        saveClans();
        return true;
    }
    
    // Распустить клан
    public boolean disbandClan(Player player) {
        if (!hasClan(player)) {
            MessageUtils.sendMessage(player, "clan.not-in-clan");
            return false;
        }
        
        String clanName = playerClans.get(player.getUniqueId());
        Clan clan = clans.get(clanName);
        
        if (!clan.isLeader(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "clan.not-leader");
            return false;
        }
        
        // Удаляем клан
        for (UUID member : clan.getMembers()) {
            playerClans.remove(member);
        }
        clans.remove(clanName);
        
        MessageUtils.sendMessage(player, "clan.disbanded", 
            "{name}", clan.getName());
        saveClans();
        return true;
    }
    
    // Вступить в клан
    public boolean joinClan(Player player, String clanName) {
        if (hasClan(player)) {
            MessageUtils.sendMessage(player, "clan.already-have");
            return false;
        }
        
        Clan clan = clans.get(clanName.toLowerCase());
        if (clan == null) {
            MessageUtils.sendMessage(player, "clan.not-found");
            return false;
        }
        
        clan.addMember(player.getUniqueId());
        playerClans.put(player.getUniqueId(), clanName.toLowerCase());
        
        MessageUtils.sendMessage(player, "clan.joined", 
            "{name}", clan.getName());
        
        // Уведомление участникам
        for (String member : clan.getOnlineMembers()) {
            Player p = org.bukkit.Bukkit.getPlayerExact(member);
            if (p != null && !p.getName().equals(player.getName())) {
                MessageUtils.sendMessage(p, "clan.member-joined",
                    "{player}", player.getName());
            }
        }
        
        saveClans();
        return true;
    }
    
    // Покинуть клан
    public boolean leaveClan(Player player) {
        if (!hasClan(player)) {
            MessageUtils.sendMessage(player, "clan.not-in-clan");
            return false;
        }
        
        String clanName = playerClans.get(player.getUniqueId());
        Clan clan = clans.get(clanName);
        
        if (clan.isLeader(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "clan.leader-cant-leave");
            return false;
        }
        
        clan.removeMember(player.getUniqueId());
        playerClans.remove(player.getUniqueId());
        
        MessageUtils.sendMessage(player, "clan.left", 
            "{name}", clan.getName());
        
        // Уведомление участникам
        for (String member : clan.getOnlineMembers()) {
            Player p = org.bukkit.Bukkit.getPlayerExact(member);
            if (p != null) {
                MessageUtils.sendMessage(p, "clan.member-left",
                    "{player}", player.getName());
            }
        }
        
        saveClans();
        return true;
    }
    
    // Проверить, есть ли у игрока клан
    public boolean hasClan(Player player) {
        return playerClans.containsKey(player.getUniqueId());
    }
    
    // Получить клан игрока
    public Clan getPlayerClan(Player player) {
        String clanName = playerClans.get(player.getUniqueId());
        return clanName != null ? clans.get(clanName) : null;
    }
    
    // Получить клан по имени
    public Clan getClan(String name) {
        return clans.get(name.toLowerCase());
    }
    
    // Список всех кланов
    public List<String> getClanList() {
        return new ArrayList<>(clans.keySet());
    }
    
    // Сохранение в файл
    public void saveClans() {
        try {
            clansConfig.set("clans", null); // Очищаем старые данные
            
            for (Clan clan : clans.values()) {
                String path = "clans." + clan.getName().toLowerCase();
                clansConfig.set(path + ".name", clan.getName());
                clansConfig.set(path + ".leader", clan.getLeader().toString());
                clansConfig.set(path + ".tag", clan.getTag());
                
                List<String> members = clan.getMembers().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
                clansConfig.set(path + ".members", members);
            }
            
            clansConfig.save(clansFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Загрузка из файла
    private void loadClans() {
        clansFile = new File(SimpleClansPlugin.getInstance().getDataFolder(), "clans.yml");
        if (!clansFile.exists()) {
            try {
                clansFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        
        // Загружаем кланы
        if (clansConfig.contains("clans")) {
            for (String key : clansConfig.getConfigurationSection("clans").getKeys(false)) {
                String path = "clans." + key;
                String name = clansConfig.getString(path + ".name");
                UUID leader = UUID.fromString(clansConfig.getString(path + ".leader"));
                
                Clan clan = new Clan(name, leader);
                
                // Загружаем участников
                List<String> members = clansConfig.getStringList(path + ".members");
                for (String member : members) {
                    UUID memberId = UUID.fromString(member);
                    if (!memberId.equals(leader)) {
                        clan.addMember(memberId);
                    }
                    playerClans.put(memberId, key);
                }
                
                clans.put(key, clan);
            }
        }
    }
}