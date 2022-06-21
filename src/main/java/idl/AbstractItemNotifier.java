package idl;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

abstract class AbstractItemNotifier {
    private final ItemChecker checker;
    private final FileConfiguration config;
    private final ChatFormatter chatFormatter;

    public AbstractItemNotifier(ItemChecker checker, FileConfiguration config, ChatFormatter chatFormatter) {
        this.checker = checker;
        this.config = config;
        this.chatFormatter = chatFormatter;
    }

    public void handle(Player player) {
        int newItems = this.checker.check(player);
        if (newItems > 0) {
            player.sendMessage(chatFormatter.format("messages.new_rewards").replace("%amount%", String.valueOf(newItems)));
        }
    }
}
