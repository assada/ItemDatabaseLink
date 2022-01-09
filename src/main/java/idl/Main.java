package idl;

import idl.DataSource.ItemDataSource;
import idl.DataSource.ItemMysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    FileConfiguration config = getConfig();
    MysqlDataSource dataSource = new MysqlDataSource(config);

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "["+this.getName()+"] Enabled " + this.getName());
        if(config.getInt("general.configVersion", 1) < 4) { //config migration
            config.set("general.configVersion", 4);
            config.set("general.checkTicks", 3600);
            config.set("general.chatPrefix", "IDL");
            this.saveConfig();
        } else {
            this.saveDefaultConfig();
        }

        ItemDataSource itemDataSource = new ItemMysqlDataSource(dataSource);
        ItemChecker itemChecker = new DatabaseItemChecker(itemDataSource);
        Listener listener;

        itemDataSource.migrate();

        if (config.getBoolean("integration.AuthMe") && getServer().getPluginManager().getPlugin("AuthMe") != null) {
            Bukkit.getLogger().info(ChatColor.GREEN + "["+this.getName()+"] AuthMe detected! Using Login event instead Join...");
            listener = new LoginListener(itemChecker, config);
        } else {
            listener = new JoinListener(itemChecker, config);
        }

        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("get").setExecutor(new GetCommand(config, itemChecker));
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new ItemCheckTask(itemChecker, config), 0L, config.getLong("general.checkTicks", 3600L));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "["+this.getName()+"] Disabled " + this.getName());
        dataSource.close();
    }
}
