package idl;

import idl.DataSource.ItemDataSource;
import idl.DataSource.ItemMysqlDataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    FileConfiguration config = getConfig();
    MysqlDataSource dataSource = new MysqlDataSource(config);
    private static Economy econ;

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Enabled " + this.getName());
        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - No Vault soft-dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (config.getInt("general.configVersion", 1) < 5) { //config migration
            if (config.getInt("general.configVersion", 1) < 4) {
                config.set("general.configVersion", 4);
                config.set("general.checkTicks", 3600);
                config.set("general.chatPrefix", "IDL");
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 4");
            }
            if (config.getInt("general.configVersion", 1) < 5) {
                config.set("general.configVersion", 5);
                config.set("mysql.autoMigration", true);
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 5");
            }
            if (config.getInt("general.configVersion", 1) < 6) {
                config.set("general.configVersion", 6);
                config.set("general.addDescriptionToItems", true);
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 6");
            }

            this.saveConfig();
        } else {
            Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Creating default config");
            this.saveDefaultConfig();
        }

        ItemDataSource itemDataSource = new ItemMysqlDataSource(dataSource, config.getString("mysql.database", "minecraft"));
        ItemChecker itemChecker = new DatabaseItemChecker(itemDataSource);
        Listener listener;

        if (config.getBoolean("mysql.autoMigration", true)) {
            itemDataSource.migrate();
        }

        if (config.getBoolean("integration.AuthMe") && getServer().getPluginManager().getPlugin("AuthMe") != null) {
            Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] AuthMe detected! Using Login event instead Join...");
            listener = new LoginListener(itemChecker, config);
        } else {
            listener = new JoinListener(itemChecker, config);
        }

        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("claim").setExecutor(new ClaimCommand(config, itemChecker, this.getEconomy()));
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new ItemCheckTask(itemChecker, config), 0L, config.getLong("general.checkTicks", 3600L));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Disabled " + this.getName());
        dataSource.close();
    }
}
