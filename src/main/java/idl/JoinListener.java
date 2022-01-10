package idl;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener extends AbstractItemNotifier implements Listener {
    public JoinListener(ItemChecker checker, FileConfiguration config) {
        super(checker, config);
    }

    @EventHandler
    public void onAuth(PlayerJoinEvent event) {
        this.handle(event.getPlayer());
    }
}
