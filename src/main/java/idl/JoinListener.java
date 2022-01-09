package idl;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final ItemChecker checker;
    private FileConfiguration config;

    public JoinListener(ItemChecker itemChecker, FileConfiguration config) {
        this.checker = itemChecker;
        this.config = config;
    }

    @EventHandler
    public void onAuth(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int newItems = this.checker.check(player);
        if (newItems > 0) {
            player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Got " + ChatColor.WHITE + newItems + ChatColor.GREEN + " new items for you. Use " + ChatColor.YELLOW + "/get" + ChatColor.GREEN + " command to get it!");
        }
    }
}
