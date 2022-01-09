package idl;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LoginListener implements Listener {
    private ItemChecker checker;

    public LoginListener(ItemChecker checker) {

        this.checker = checker;
    }

    @EventHandler
    public void onAuth(LoginEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Success auth!");
        this.checker.check();
    }
}
