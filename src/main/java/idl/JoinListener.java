package idl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private ItemChecker itemChecker;

    public JoinListener(ItemChecker itemChecker) {

        this.itemChecker = itemChecker;
    }

    @EventHandler
    public void onAuth(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Success join!");
        this.itemChecker.check();
    }
}
