package idl;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LoginListener extends AbstractItemNotifier implements Listener {
    public LoginListener(ItemChecker checker, FileConfiguration config, ChatFormatter chatFormatter) {
        super(checker, config, chatFormatter);
    }

    @EventHandler
    public void onAuth(LoginEvent event) {
        this.handle(event.getPlayer());
    }
}
