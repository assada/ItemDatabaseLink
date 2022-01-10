package idl;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ItemCheckTask extends AbstractItemNotifier implements Runnable {

    public ItemCheckTask(ItemChecker checker, FileConfiguration config) {
        super(checker, config);
    }

    @Override
    public void run() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            this.handle(player.getPlayer());
        }
    }
}
