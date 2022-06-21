package idl;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SimpleChatFormatter implements ChatFormatter {
    private final FileConfiguration config;

    public SimpleChatFormatter(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public String format(String messageKey) {
        String message = config.getString(messageKey);
        if(null == message) {
            message = "&4Error! Message key '%s' does not found".formatted(messageKey);
        }

        return ChatColor.translateAlternateColorCodes('&', this.config.getString("general.chatPrefix") + "&r " + message);
    }
}
