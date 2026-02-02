package me.day2s.simpleclans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.List;

public class ClanCommand implements CommandExecutor {
    
    private ClanManager clanManager;
    
    public ClanCommand() {
        this.clanManager = SimpleClansPlugin.getInstance().getClanManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /clan create <название>");
                    return true;
                }
                clanManager.createClan(player, args[1]);
                break;
                
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /clan join <название>");
                    return true;
                }
                clanManager.joinClan(player, args[1]);
                break;
                
            case "leave":
                clanManager.leaveClan(player);
                break;
                
            case "disband":
                clanManager.disbandClan(player);
                break;
                
            case "info":
                if (args.length > 1) {
                    showClanInfo(player, args[1]);
                } else {
                    showPlayerClanInfo(player);
                }
                break;
                
            case "list":
                showClanList(player);
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== SimpleClans Помощь ===");
        player.sendMessage(ChatColor.YELLOW + "/clan create <название>" + ChatColor.WHITE + " - Создать клан");
        player.sendMessage(ChatColor.YELLOW + "/clan join <название>" + ChatColor.WHITE + " - Вступить в клан");
        player.sendMessage(ChatColor.YELLOW + "/clan leave" + ChatColor.WHITE + " - Покинуть клан");
        player.sendMessage(ChatColor.YELLOW + "/clan disband" + ChatColor.WHITE + " - Распустить клан");
        player.sendMessage(ChatColor.YELLOW + "/clan info [название]" + ChatColor.WHITE + " - Информация о клане");
        player.sendMessage(ChatColor.YELLOW + "/clan list" + ChatColor.WHITE + " - Список кланов");
    }
    
    private void showClanInfo(Player player, String clanName) {
        Clan clan = clanManager.getClan(clanName);
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Клан не найден!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== Клан: " + clan.getName() + " ===");
        player.sendMessage(ChatColor.YELLOW + "Тег: " + ChatColor.WHITE + "[" + clan.getTag() + "]");
        player.sendMessage(ChatColor.YELLOW + "Лидер: " + ChatColor.WHITE + 
            org.bukkit.Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        player.sendMessage(ChatColor.YELLOW + "Участников: " + ChatColor.WHITE + clan.getSize());
        player.sendMessage(ChatColor.YELLOW + "Онлайн: " + ChatColor.GREEN + 
            String.join(", ", clan.getOnlineMembers()));
    }
    
    private void showPlayerClanInfo(Player player) {
        Clan clan = clanManager.getPlayerClan(player);
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане!");
            return;
        }
        showClanInfo(player, clan.getName());
    }
    
    private void showClanList(Player player) {
        List<String> clans = clanManager.getClanList();
        if (clans.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Нет созданных кланов.");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== Список кланов ===");
        for (String clanKey : clans) {
            Clan clan = clanManager.getClan(clanKey);
            player.sendMessage(ChatColor.YELLOW + "- " + clan.getName() + 
                ChatColor.WHITE + " [" + clan.getTag() + "] " + 
                ChatColor.GRAY + "(" + clan.getSize() + " чел.)");
        }
    }
}