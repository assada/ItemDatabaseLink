package idl;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

abstract class AbstractItemNotifier {
    private final ItemChecker checker;
    private final FileConfiguration config;

    public AbstractItemNotifier(ItemChecker checker, FileConfiguration config) {
        this.checker = checker;
        this.config = config;
    }

    public void handle(Player player) {
        int newItems = this.checker.check(player);
        if (newItems > 0) {
            player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Got " + ChatColor.WHITE + newItems + ChatColor.GREEN + " new items for you. Use " + ChatColor.YELLOW + "/claim" + ChatColor.GREEN + " command to claim it!");
        }
    }
}
