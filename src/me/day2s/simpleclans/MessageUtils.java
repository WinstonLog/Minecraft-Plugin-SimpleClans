package me.day2s.simpleclans;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageUtils {
    
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static void sendMessage(CommandSender sender, String key) {
        FileConfiguration messages = SimpleClansPlugin.getInstance().getMessages();
        String message = messages.getString(key);
        
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(color(message));
        } else {
            // Дефолтные сообщения
            switch (key) {
                case "clan.already-have":
                    sender.sendMessage(color("&cУ вас уже есть клан!"));
                    break;
                case "clan.exists":
                    sender.sendMessage(color("&cКлан с таким названием уже существует!"));
                    break;
                case "clan.created":
                    sender.sendMessage(color("&aКлан успешно создан!"));
                    break;
                case "clan.not-in-clan":
                    sender.sendMessage(color("&cВы не состоите в клане!"));
                    break;
                case "clan.not-found":
                    sender.sendMessage(color("&cКлан не найден!"));
                    break;
                case "clan.joined":
                    sender.sendMessage(color("&aВы вступили в клан!"));
                    break;
                case "clan.left":
                    sender.sendMessage(color("&aВы покинули клан!"));
                    break;
                case "clan.disbanded":
                    sender.sendMessage(color("&aКлан распущен!"));
                    break;
                case "clan.not-leader":
                    sender.sendMessage(color("&cТолько лидер может это сделать!"));
                    break;
                case "clan.leader-cant-leave":
                    sender.sendMessage(color("&cЛидер не может покинуть клан! Сначала распустите его."));
                    break;
                case "clan.member-joined":
                    sender.sendMessage(color("&aНовый участник присоединился к клану!"));
                    break;
                case "clan.member-left":
                    sender.sendMessage(color("&cИгрок покинул клан!"));
                    break;
            }
        }
    }
    
    public static void sendMessage(CommandSender sender, String key, String... replacements) {
        FileConfiguration messages = SimpleClansPlugin.getInstance().getMessages();
        String message = messages.getString(key);
        
        if (message == null || message.isEmpty()) {
            sendMessage(sender, key);
            return;
        }
        
        // Замена плейсхолдеров
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        
        sender.sendMessage(color(message));
    }
}