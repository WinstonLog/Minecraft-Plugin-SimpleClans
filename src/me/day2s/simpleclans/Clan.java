package me.day2s.simpleclans;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private String name;
    private UUID leader;
    private List<UUID> members;
    private String tag;
    
    public Clan(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
        this.tag = name.substring(0, Math.min(3, name.length())).toUpperCase();
    }
    
    // Геттеры
    public String getName() {
        return name;
    }
    
    public UUID getLeader() {
        return leader;
    }
    
    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }
    
    public String getTag() {
        return tag;
    }
    
    // Методы управления
    public boolean addMember(UUID player) {
        if (!members.contains(player)) {
            members.add(player);
            return true;
        }
        return false;
    }
    
    public boolean removeMember(UUID player) {
        return members.remove(player);
    }
    
    public boolean isMember(UUID player) {
        return members.contains(player);
    }
    
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }
    
    public void setLeader(UUID newLeader) {
        this.leader = newLeader;
    }
    
    public int getSize() {
        return members.size();
    }
    
    // Получить имена онлайн участников
    public List<String> getOnlineMembers() {
        List<String> online = new ArrayList<>();
        for (UUID member : members) {
            Player player = Bukkit.getPlayer(member);
            if (player != null && player.isOnline()) {
                online.add(player.getName());
            }
        }
        return online;
    }
}