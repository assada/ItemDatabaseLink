package idl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ItemCheckTask implements Runnable {
    private ItemChecker checker;
    private FileConfiguration config;

    public ItemCheckTask(ItemChecker checker, FileConfiguration config) {
        this.checker = checker;
        this.config = config;
    }

    @Override
    public void run() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            int newItems = this.checker.check(player);
            if (newItems > 0) {
                player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Got " + ChatColor.WHITE + newItems + ChatColor.GREEN + " new items for you. Use " + ChatColor.YELLOW + "/get" + ChatColor.GREEN + " command to get it!");
            }
        }
    }
}
